package com.godpalace.waiter.execute;

import java.util.ArrayList;
import java.util.List;

public class ErrorMgr {
    private final List<Error> errors;
    private boolean isError;
    public ErrorMgr() {
        errors = new ArrayList<>();
        isError = false;
    }

    public void addError(int index, ErrorType error) {
        isError = true;
        errors.add(new Error(index, error));
    }

    public boolean isError() {
        return isError;
    }

    public String getErrorMessage() {
        StringBuilder sb = new StringBuilder();
        for (Error error : errors) {
            if (error!= null) {
                String errorString = "未知错误";
                if (error.error == ErrorType.ERROR_STRUCTURE) {
                    errorString = "命令格式错误";
                }
                else if (error.error == ErrorType.ERROR_COMMAND) {
                    errorString = "命令不存在";
                }
                else if (error.error == ErrorType.ERROR_VALUE) {
                    errorString = "参数错误";
                }

                sb.append("[第").append(error.index).append("行] ").append(errorString).append("\n");
            }
        }
        return sb.toString();
    }

    public static class Error {
        public int index;
        public ErrorType error;

        public Error(int index, ErrorType error) {
            this.index = index;
            this.error = error;
        }
    }

    public enum ErrorType {
        ERROR_COMMAND,
        ERROR_STRUCTURE,
        ERROR_VALUE
    }
}
