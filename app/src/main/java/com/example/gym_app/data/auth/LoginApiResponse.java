package com.example.gym_app.data.auth;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

class LoginApiResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;

    @SerializedName("email")
    private String email;

    @SerializedName("username")
    private String username;

    @SerializedName("name")
    private String name;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("user")
    private ApiUser user;

    @SerializedName("data")
    private ApiUser data;

    String getMessage() {
        return message;
    }

    String getResolvedToken() {
        if (!TextUtils.isEmpty(accessToken)) {
            return accessToken;
        }
        if (!TextUtils.isEmpty(token)) {
            return token;
        }
        if (!TextUtils.isEmpty(refreshToken)) {
            return refreshToken;
        }
        return null;
    }

    String getResolvedEmail() {
        if (!TextUtils.isEmpty(email)) {
            return email;
        }
        if (!TextUtils.isEmpty(username)) {
            return username;
        }
        if (!TextUtils.isEmpty(name) && name.contains("@")) {
            return name;
        }
        ApiUser resolvedUser = firstNonNull(user, data);
        if (resolvedUser != null) {
            String resolved = resolvedUser.getEmail();
            if (!TextUtils.isEmpty(resolved)) {
                return resolved;
            }
        }
        return null;
    }

    String getResolvedDisplayName() {
        if (!TextUtils.isEmpty(fullName)) {
            return fullName;
        }
        if (!TextUtils.isEmpty(name)) {
            return name;
        }
        ApiUser resolvedUser = firstNonNull(user, data);
        if (resolvedUser != null) {
            String displayName = resolvedUser.getDisplayName();
            if (!TextUtils.isEmpty(displayName)) {
                return displayName;
            }
        }
        return null;
    }

    private ApiUser firstNonNull(ApiUser first, ApiUser second) {
        return first != null ? first : second;
    }

    static class ApiUser {

        @SerializedName("email")
        private String email;

        @SerializedName("username")
        private String username;

        @SerializedName("name")
        private String name;

        @SerializedName("fullName")
        private String fullName;

        String getEmail() {
            if (!TextUtils.isEmpty(email)) {
                return email;
            }
            if (!TextUtils.isEmpty(username)) {
                return username;
            }
            if (!TextUtils.isEmpty(name) && name.contains("@")) {
                return name;
            }
            if (!TextUtils.isEmpty(fullName) && fullName.contains("@")) {
                return fullName;
            }
            return null;
        }

        String getDisplayName() {
            if (!TextUtils.isEmpty(fullName)) {
                return fullName;
            }
            if (!TextUtils.isEmpty(name)) {
                return name;
            }
            if (!TextUtils.isEmpty(username)) {
                return username;
            }
            if (!TextUtils.isEmpty(email)) {
                return email;
            }
            return null;
        }
    }
}
