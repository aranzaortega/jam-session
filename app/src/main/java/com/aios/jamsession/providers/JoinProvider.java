package com.aios.jamsession.providers;

import com.aios.jamsession.models.Join;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class JoinProvider {

    CollectionReference mCollection;

    public JoinProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Joiners");
    }

    public Task<Void> create(Join join){
        // To get its own id
        DocumentReference documentReference = mCollection.document();
        String id = documentReference.getId();
        join.setId(id);

        return documentReference.set(join);
    }

    public Query getJoinsByPost(String idPost){
        return mCollection.whereEqualTo("idPost", idPost);
    }

    public Query getJoinByPostAndUser(String idPost, String idUser){
        return mCollection.whereEqualTo("idPost", idPost).whereEqualTo("idUser", idUser);
    }

    public Task<Void> delete(String id){
        return mCollection.document(id).delete();
    }
}
