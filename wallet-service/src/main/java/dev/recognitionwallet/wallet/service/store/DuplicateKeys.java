package dev.recognitionwallet.wallet.service.store;

import org.springframework.dao.DuplicateKeyException;

/**
 * Detects a unique/primary-key violation anywhere in an exception's cause chain
 * spring Data JDBC may wrap DuplicateKeyException in DbActionExecutionException
 * 
 */

public final class DuplicateKeys{

    private DuplicateKeys(){}

    public static boolean isDuplicateKey(Throwable throwable){
        for (Throwable cause = throwable; cause !=null; cause= cause.getCause()){
            if (cause instanceof DuplicateKeyException){
                return true;
            }
        }
        return false;
    }
}