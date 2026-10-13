package dev.recognitionwallet.wallet.events.v1;

import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.SchemaCompatibility;
import org.apache.avro.SchemaCompatibility.SchemaCompatibilityType;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// Which schema changes are safe? Demonstrated with a throwaway "Probe" schema.
class SchemaEvolutionTest {

    private static final Schema V1 = new Schema.Parser().parse("""
            {"type": "record", "name": "Probe", "fields": [
                {"name": "id", "type": "string"}
            ]}
            """);

    private static final Schema V2_OPTIONAL_FIELD = new Schema.Parser().parse("""
            {"type": "record", "name": "Probe", "fields": [
                {"name": "id", "type": "string"},
                {"name": "note", "type": ["null", "string"], "default": null}
            ]}
            """);

    private static final Schema V2_REQUIRED_FIELD = new Schema.Parser().parse("""
            {"type": "record", "name": "Probe", "fields": [
                {"name": "id", "type": "string"},
                {"name": "note", "type": "string"}
            ]}
            """);

    @Test
    void newReaderWithOptionalFieldReadsOldMessages() throws IOException {
        byte[] oldMessage = write(V1, record(V1, "p-1"));

        GenericRecord read = read(oldMessage, V1, V2_OPTIONAL_FIELD);

        assertThat(read.get("id")).hasToString("p-1");
        assertThat(read.get("note")).isNull();
    }

    @Test
    void newReaderWithRequiredFieldCannotReadOldMessages() throws IOException {
        byte[] oldMessage = write(V1, record(V1, "p-1"));

        assertThatThrownBy(() -> read(oldMessage, V1, V2_REQUIRED_FIELD))
                .isInstanceOf(AvroTypeException.class)
                .hasMessageContaining("note");
    }

    @Test
    void oldReaderIgnoresFieldsAddedLater() throws IOException {
        GenericRecord newRecord = new GenericData.Record(V2_OPTIONAL_FIELD);
        newRecord.put("id", "p-2");
        newRecord.put("note", "added in v2");
        byte[] newMessage = write(V2_OPTIONAL_FIELD, newRecord);

        GenericRecord read = read(newMessage, V2_OPTIONAL_FIELD, V1);

        assertThat(read.get("id")).hasToString("p-2");
    }

    @Test
    void compatibilityCheckerAgreesWithTheExperiments() {
        assertThat(compatibility(V2_OPTIONAL_FIELD, V1)).isEqualTo(SchemaCompatibilityType.COMPATIBLE);
        assertThat(compatibility(V2_REQUIRED_FIELD, V1)).isEqualTo(SchemaCompatibilityType.INCOMPATIBLE);
        assertThat(compatibility(V1, V2_OPTIONAL_FIELD)).isEqualTo(SchemaCompatibilityType.COMPATIBLE);
    }

    @Test
    void everyNullableFieldInOurEventsHasADefault() {
        List<Schema> ourSchemas = List.of(
                AiUsageIngested.getClassSchema(),
                JudgeRequested.getClassSchema(),
                QualityScored.getClassSchema(),
                RewardDrawn.getClassSchema()
        );

        for (Schema schema : ourSchemas) {
            for (Schema.Field field : schema.getFields()) {
                if (field.schema().getType() == Schema.Type.UNION) {
                    assertThat(field.hasDefaultValue())
                            .as("%s.%s is nullable and must have a default", schema.getName(), field.name())
                            .isTrue();
                }
            }
        }
    }
    private static SchemaCompatibilityType compatibility(Schema reader, Schema writer) {
        return SchemaCompatibility.checkReaderWriterCompatibility(reader, writer).getType();
    }

    private static GenericRecord record(Schema schema, String id) {
        GenericRecord record = new GenericData.Record(schema);
        record.put("id", id);
        return record;
    }

    private static byte[] write(Schema schema, GenericRecord record) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        new GenericDatumWriter<GenericRecord>(schema).write(record, encoder);
        encoder.flush();
        return out.toByteArray();
    }

    private static GenericRecord read(byte[] bytes, Schema writer, Schema reader) throws IOException {
        return new GenericDatumReader<GenericRecord>(writer, reader)
                .read(null, DecoderFactory.get().binaryDecoder(bytes, null));
    }
}