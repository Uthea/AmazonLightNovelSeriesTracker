package com.vindra.seriestracker.interceptor;

import com.vindra.seriestracker.entity.KindleSeries;
import com.vindra.seriestracker.webhook.discord.KindleResponse;
import com.vindra.seriestracker.entity.Kindle;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KindleInterceptor extends EmptyInterceptor {


    @Autowired
    KindleResponse kindleResponse;

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {

        // if update operation on kindle entity
        if(entity instanceof Kindle && previousState != null) {
            // notify webhook
            kindleResponse.onKindleUpdate(id, currentState, previousState, propertyNames);
        }


        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {


        if(entity instanceof KindleSeries) {
            System.out.println("Inserting on kindle series");
            kindleResponse.onNewKindleSeries(id.toString(), state, propertyNames);

        }

        return super.onSave(entity, id, state, propertyNames, types);
    }

    @Override
    public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {

        int snapShotSize = ((ArrayList) ((PersistentBag) collection).getStoredSnapshot()).size();
        int currentSize = ((PersistentBag) collection).size();

        // check if current collection size growing
        if ( currentSize != 0 && snapShotSize < currentSize
                && ((PersistentBag) collection).get(0) instanceof Kindle) {

                List<Kindle> snapshot = (List<Kindle>) ((PersistentBag) collection).getStoredSnapshot();

                ((PersistentBag) collection).forEach( (kindle) -> {

                    // dont trigger webhook if kindle already exist in stored snapshot
                    if ( !snapshot.contains(kindle) ) {
                        kindleResponse.onKindleSeriesCollectionUpdate((Kindle) kindle);
                    }

                });
        }




        super.onCollectionUpdate(collection, key);
    }
}
