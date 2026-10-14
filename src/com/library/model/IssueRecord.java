package com.library.model;

public class IssueRecord {
    private String id;
    private String bookId;
    private String memberId;
    private String issueDate;
    private String dueDate;
    private String returnDate;

    public IssueRecord(String id, String bookId, String memberId, String issueDate, String dueDate, String returnDate) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public String getId() {
        return id;
    }

    public String getBookId() {
        return bookId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String[] toRow() {
        return new String[] { id, bookId, memberId, issueDate, dueDate, returnDate };
    }
}