package com.community.backend.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
@Entity
@Table(name="help_request_tags",uniqueConstraints=@UniqueConstraint(name="uk_help_request_tag",columnNames={"help_request_id","tag"}))
public class HelpRequestTag {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name="help_request_id",nullable=false)
    private HelpRequest helpRequest;
    @Column(nullable=false,length=40)
    private String tag;
    public Long getId() { return id; }
    public HelpRequest getHelpRequest() { return helpRequest; }
    public String getTag() { return tag; }
    public void setHelpRequest(HelpRequest helpRequest) { this.helpRequest=helpRequest; }
    public void setTag(String tag) { this.tag=tag; }
}
