/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package myutil.externalSearch;

/**
 * JDialogSearchBox format of return message from CVE database Creation:
 * 12/03/2015
 * 
 * @version 1.0 11/03/2015
 * @author Huy TRUONG
 */
public class Record {
    private String summary;
    private String cve_id;
    private String pub_date;
    private String mod_date;
    private String score;
    private String access_vector;
    private String access_complexity;
    private String authentication;
    private String confidentiality_impact;
    private String integrity_impact;
    private String availability_impact;
    private String gen_date;
    private String cwe_id;
    private String ref_type;
    private String source;
    private String link;
    private String name;

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
        return "summary='" + summary + '\n' + ", cve_id='" + cve_id + '\n' + ", pub_date='" + pub_date + '\n'
                + ", mod_date='" + mod_date + '\n' + ", score='" + score + '\n' + ", access_vector='" + access_vector
                + '\n' + ", access_complexity='" + access_complexity + '\n' + ", authentication='" + authentication
                + '\n' + ", confidentiality_impact='" + confidentiality_impact + '\n' + ", integrity_impact='"
                + integrity_impact + '\n' + ", availability_impact='" + availability_impact + '\n' + ", gen_date='"
                + gen_date + '\n' + ", cwe_id='" + cwe_id + '\n' + ", ref_type='" + ref_type + '\n' + ", source='"
                + source + '\n' + ", link='" + link + '\n' + ", name='" + name + '\n';

    }
}
