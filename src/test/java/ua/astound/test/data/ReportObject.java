package ua.astound.test.data;

import java.util.List;

import lombok.Data;

@Data
public class ReportObject {

    private String startTime;
    private List<Test> tests;

    @Data
    public static class Test {
        private String name;
        private String description;
        private String status;
        private String exception;
    }

}
