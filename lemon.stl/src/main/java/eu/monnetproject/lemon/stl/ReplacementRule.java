/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.lemon.stl;

import java.util.regex.Pattern;

/**
 *
 * @author tobwun
 */
public class ReplacementRule {
    private Pattern pattern;
    private Integer sourceGroup;
    private Integer targetGroup;
    private Boolean updateForm;
    private Boolean removeRule;
    /**
     * 
     * @param pattern
     * @param sourceGroup
     * @param targetGroup
     * @param removeRule
     * @param updateForm - not recommended (should be false)
     */
    public ReplacementRule(String pattern,
            Integer sourceGroup, Integer targetGroup,
            Boolean removeRule, Boolean updateForm) {
        this.pattern = Pattern.compile(pattern);
        this.sourceGroup = sourceGroup;
        this.targetGroup = targetGroup;
        this.updateForm = updateForm;
        this.removeRule = removeRule;
    }
    public Pattern getPattern() {
        return pattern;
    }
    public Integer getSourceGroup() {
        return sourceGroup;
    }
    public Integer getTargetGroup() {
        return targetGroup;
    }
    public Boolean removeRule() {
        return removeRule;
    }
    public Boolean updateForm() {
        return updateForm;
    }
}
