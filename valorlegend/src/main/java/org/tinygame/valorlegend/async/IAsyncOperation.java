package org.tinygame.valorlegend.async;

/**
 * Async operation contract.
 */
public interface IAsyncOperation {
    default int getBindId() {
        return 0;
    }

    void doAsync();

    default void doFinish() {
    }
}
