package com.aios.jamsession.providers;

import com.aios.jamsession.models.Comment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CommentProvider {

    CollectionReference mCollection;

    public CommentProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Comments");
    }

    public Query getCommentsByPost(String idPost){
        return mCollection.whereEqualTo("idPost", idPost);
    }

    public Task<Void> create(Comment comment){
        return mCollection.document().set(comment);
    }
}
