package uk.me.desiderio.shiftt.data.database;

import android.util.Log;

import java.util.List;
import java.util.stream.Collectors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import uk.me.desiderio.shiftt.data.database.model.QueryTrendEnt;
import uk.me.desiderio.shiftt.data.database.model.TrendEnt;
import uk.me.desiderio.shiftt.data.database.model.TrendPlaceEnt;

@Dao
public abstract class TrendsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertTrendEnt(TrendEnt trends);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertTrendPlaceEnt(TrendPlaceEnt trendPlace);


    public void insertTrendEntList(List<TrendEnt> trends) {
        trends.stream()
                .forEach(trendEnt -> {
                    if(trendEnt.place != null) {
                        insertTrendPlaceEnt(trendEnt.place);
                        trendEnt.placeName = trendEnt.place.placeName;

                    }
                    insertTrendEnt(trendEnt);
                });
    }

    // QUERY

    @Transaction
    @Query("SELECT * FROM trend ORDER BY tweet_volume DESC")
    public abstract LiveData<List<QueryTrendEnt>> getAllQueryTrends();


    public LiveData<List<TrendEnt>> getAllTrends() {
        LiveData<List<QueryTrendEnt>> queryData = getAllQueryTrends();

        return Transformations.map(queryData, queryDataList ->
            queryDataList.stream()
                    .map(queryTrendEnt -> queryTrendEnt.getPopulatedTweetEnt())
                    .collect(Collectors.toList())
        );
    }
}
