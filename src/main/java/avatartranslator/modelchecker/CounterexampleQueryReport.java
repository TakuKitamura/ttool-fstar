package avatartranslator.modelchecker;

public class CounterexampleQueryReport {
    private String name;
    private String query;
    private String report;
    
    public CounterexampleQueryReport(String name) {
        this.name = name;
    }
    
    public CounterexampleQueryReport(String name, String query) {
        this.name = name;
        this.query = query;
    }
    
    public CounterexampleQueryReport(String name, String query, String report) {
        this.name = name;
        this.query = query;
        this.report = report;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getQuery() {
        return this.query;
    }
    
    public String getReport() {
        return this.report;
    }
    
    public void setReport(String report) {
        this.report = report;
    }

}
