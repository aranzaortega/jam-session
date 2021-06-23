package com.aios.jamsession.providers;

import com.aios.jamsession.models.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MessageProvider {

    CollectionReference mCollection;

    public MessageProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Messages");
    }

    public Task<Void> create(Message message){
        DocumentReference documentReference = mCollection.document();
        message.setId(documentReference.getId());
        return documentReference.set(message);
    }

    public Query getMessagesByChat(String idChat){
        return mCollection.whereEqualTo("idChat", idChat).orderBy("timestamp", Query.Direction.ASCENDING);
    }

}
