package com.generated.qualityTrace.types;

/** 放行提交请求体：actorId 可空，缺省时取认证身份。 */
public record ReleaseSubmissionPayload(String actorId, String note) {}
