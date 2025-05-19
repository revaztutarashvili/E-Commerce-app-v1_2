package com.example.E_Commerce.app.v1_2.dto;
//კაროჩე აქ  ვიყენებ ჯენერიქს ამ კლასში, სადაც T მოქმედებს როგორც ადგილი, რომელშიც მოგვიანებით ჩაჯდება რეალური მონაცემთა ტიპი (მაგალითად, ProductDto, List<ProductDto> და ასე შემდეგ )
public class ApiResponse<T> {
    private boolean success;

    private String message;

    private T data;

    // Constructors
    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // Static დამხმარე მეთოდები
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message);
    }
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
}
