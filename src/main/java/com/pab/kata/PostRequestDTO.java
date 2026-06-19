package com.pab.kata;

public record PostRequestDTO(Long userId, String title, String post) {

    @Override
    public String toString() {
        return "PostRequestDTO{" +
                "userId=" + userId +
                ", title='" + title + '\'' +
                ", post='" + post + '\'' +
                '}';
    }
}


