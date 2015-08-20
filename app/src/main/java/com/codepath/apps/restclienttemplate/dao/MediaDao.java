package com.codepath.apps.restclienttemplate.dao;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import com.codepath.apps.restclienttemplate.dao.Media;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table MEDIA.
*/
public class MediaDao extends AbstractDao<Media, Long> {

    public static final String TABLENAME = "MEDIA";

    /**
     * Properties of entity Media.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property InternalId = new Property(0, Long.class, "internalId", true, "INTERNAL_ID");
        public final static Property Id = new Property(1, Long.class, "id", false, "ID");
        public final static Property Type = new Property(2, String.class, "type", false, "TYPE");
        public final static Property Media_url = new Property(3, String.class, "media_url", false, "MEDIA_URL");
        public final static Property InTweets = new Property(4, Long.class, "inTweets", false, "IN_TWEETS");
    };

    private Query<Media> twitter_MediaListQuery;

    public MediaDao(DaoConfig config) {
        super(config);
    }
    
    public MediaDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MEDIA' (" + //
                "'INTERNAL_ID' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: internalId
                "'ID' INTEGER," + // 1: id
                "'TYPE' TEXT," + // 2: type
                "'MEDIA_URL' TEXT," + // 3: media_url
                "'IN_TWEETS' INTEGER);"); // 4: inTweets
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MEDIA'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Media entity) {
        stmt.clearBindings();
 
        Long internalId = entity.getInternalId();
        if (internalId != null) {
            stmt.bindLong(1, internalId);
        }
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(2, id);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(3, type);
        }
 
        String media_url = entity.getMedia_url();
        if (media_url != null) {
            stmt.bindString(4, media_url);
        }
 
        Long inTweets = entity.getInTweets();
        if (inTweets != null) {
            stmt.bindLong(5, inTweets);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Media readEntity(Cursor cursor, int offset) {
        Media entity = new Media( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // internalId
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // type
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // media_url
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4) // inTweets
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Media entity, int offset) {
        entity.setInternalId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setType(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setMedia_url(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setInTweets(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Media entity, long rowId) {
        entity.setInternalId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Media entity) {
        if(entity != null) {
            return entity.getInternalId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "mediaList" to-many relationship of Twitter. */
    public List<Media> _queryTwitter_MediaList(Long inTweets) {
        synchronized (this) {
            if (twitter_MediaListQuery == null) {
                QueryBuilder<Media> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.InTweets.eq(null));
                twitter_MediaListQuery = queryBuilder.build();
            }
        }
        Query<Media> query = twitter_MediaListQuery.forCurrentThread();
        query.setParameter(0, inTweets);
        return query.list();
    }

}