package com.azatkhaliullin.aws.dto;

/**
 * Available languages.
 */
public enum Language {

    RU("ru", "ru-RU"), EN("en", "en-US");

    /**
     * Language code in AWS.
     */
    private final String awsTranslateValue;
    private final String awsPollyValue;


    Language(String awsTranslateValue, String awsPollyValue) {
        this.awsTranslateValue = awsTranslateValue;
        this.awsPollyValue = awsPollyValue;
    }

    public String getAwsTranslateValue() {
        return awsTranslateValue;
    }

    public String getAwsPollyValue() {
        return awsPollyValue;
    }

}
