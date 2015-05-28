package myutil.externalSearch;

/**
 * Created by trhuy on 5/12/15.
 */
public class Record {
    //private String id;
    //private String n;
    private String summary;
    private String cve_id ;
    private String pub_date ;
    private String mod_date ;
    private String score ;
    private String access_vector ;
    private String access_complexity ;
    private String authentication  ;
    private String confidentiality_impact ;
    private String integrity_impact  ;
    private String availability_impact ;
    private String gen_date;
    private String cwe_id ;
    private String ref_type ;
    private String source;
    private String link ;
    private String name ;


    public String getCve_id() {
        return cve_id;
    }

    public void setCve_id(String cve_id) {
        this.cve_id = cve_id;
    }

    public String getPub_date() {
        return pub_date;
    }

    public void setPub_date(String pub_date) {
        this.pub_date = pub_date;
    }

    public String getMod_date() {
        return mod_date;
    }

    public void setMod_date(String mod_date) {
        this.mod_date = mod_date;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getAccess_vector() {
        return access_vector;
    }

    public void setAccess_vector(String access_vector) {
        this.access_vector = access_vector;
    }

    public String getAccess_complexity() {
        return access_complexity;
    }

    public void setAccess_complexity(String access_complexity) {
        this.access_complexity = access_complexity;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getConfidentiality_impact() {
        return confidentiality_impact;
    }

    public void setConfidentiality_impact(String confidentiality_impact) {
        this.confidentiality_impact = confidentiality_impact;
    }

    public String getIntegrity_impact() {
        return integrity_impact;
    }

    public void setIntegrity_impact(String integrity_impact) {
        this.integrity_impact = integrity_impact;
    }

    public String getAvailability_impact() {
        return availability_impact;
    }

    public void setAvailability_impact(String availability_impact) {
        this.availability_impact = availability_impact;
    }

    public String getGen_date() {
        return gen_date;
    }

    public void setGen_date(String gen_date) {
        this.gen_date = gen_date;
    }

    public String getCwe_id() {
        return cwe_id;
    }

    public void setCwe_id(String cwe_id) {
        this.cwe_id = cwe_id;
    }

    public String getRef_type() {
        return ref_type;
    }

    public void setRef_type(String ref_type) {
        this.ref_type = ref_type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return
                "summary='" + summary + '\n' +
                ", cve_id='" + cve_id + '\n' +
                ", pub_date='" + pub_date + '\n' +
                ", mod_date='" + mod_date + '\n' +
                ", score='" + score + '\n' +
                ", access_vector='" + access_vector + '\n' +
                ", access_complexity='" + access_complexity + '\n' +
                ", authentication='" + authentication + '\n' +
                ", confidentiality_impact='" + confidentiality_impact + '\n' +
                ", integrity_impact='" + integrity_impact + '\n' +
                ", availability_impact='" + availability_impact + '\n' +
                ", gen_date='" + gen_date + '\n' +
                ", cwe_id='" + cwe_id + '\n' +
                ", ref_type='" + ref_type + '\n' +
                ", source='" + source + '\n' +
                ", link='" + link + '\n' +
                ", name='" + name + '\n' ;

    }
}
