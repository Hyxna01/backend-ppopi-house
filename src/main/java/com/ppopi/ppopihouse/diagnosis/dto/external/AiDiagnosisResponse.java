package com.ppopi.ppopihouse.diagnosis.dto.external;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiDiagnosisResponse {

    private String disease;
    private String triage;

    @JsonAlias({"triage_confidence", "triageConfidence"})
    private float triageConfidence;

    @JsonAlias({"family_label", "familyLabel", "affected_area", "affectedArea"})
    private String familyLabel;

    @JsonAlias({"guidance_message", "guidanceMessage"})
    private String guidanceMessage;

    @JsonAlias({"guidance_action", "guidanceAction"})
    private String guidanceAction;

    @JsonAlias({"guidance_warning", "guidanceWarning"})
    private String guidanceWarning;
}