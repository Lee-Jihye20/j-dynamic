package model;

import java.sql.Timestamp;

public class Post {
    private int id;
    private int userId;
    private String username;
    private String content;
    private boolean isSuccess;   // ← 追加
    private int reactionc;       // ← いいね数用
    private Timestamp createdAt;

    // --- Getter & Setter ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    // ★ ELから呼ばれるのでメソッド名は「isSuccess()」にする
    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        this.isSuccess = success;
    }

    public int getReactionc() {
        return reactionc;
    }

    public void setReactionc(int reactionc) {
        this.reactionc = reactionc;
    }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
}
