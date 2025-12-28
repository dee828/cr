package com.example.scheduler.request;

import jakarta.validation.constraints.NotBlank;

public class CronJobRequest {
    @NotBlank
    private String group;

    @NotBlank
    private String name;

    private String description;

    private String cronExpression;

    public @NotBlank String getGroup() {
        return group;
    }

    public void setGroup(@NotBlank String group) {
        this.group = group;
    }

    public @NotBlank String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @Override
    public String toString() {
        return "CronJobRequest{" +
                "group='" + group + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                '}';
    }
}
