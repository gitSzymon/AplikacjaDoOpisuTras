package com.example.aplikacjadoopisutras;

import android.content.Context;
import android.os.AsyncTask;
import android.speech.tts.Voice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplikacjadoopisutras.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.recyclerview.widget.RecyclerView;
import logic.DatabaseClient;
import logic.Description;
import logic.LocationPoint;
import logic.Photo;
import logic.Point;
import logic.Route;
import logic.VoiceMessage;

public class PointListAdapter extends RecyclerView.Adapter<PointListAdapter.ViewHolder> {

    private List<Route> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    public static final int DELETE_DESCRIPTION = 1;
    public static final int DELETE_VOICE = 2;
    public static final int DELETE_PHOTO = 3;
    public static final int DELETE_LOCATION_POINT = 4;
    private boolean isSelectedAll;


    // data is passed into the constructor
    PointListAdapter(Context context, List<Route> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.list_item_route, parent, false);
        //mInflater.inflate(R.layout.list_item_route, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.textRouteId = v.findViewById(R.id.route_id);
        viewHolder.textRouteName = v.findViewById(R.id.route_name);
        viewHolder.showCheckBox = v.findViewById(R.id.checkBox);
        viewHolder.btnDelete = v.findViewById(R.id.btn_delete);
        viewHolder.textDate = v.findViewById(R.id.txt_date);
        viewHolder.txtStats = v.findViewById(R.id.txt_stats);
        return viewHolder;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Route route = mData.get(position);

        TextView textRouteId = holder.textRouteId;
        textRouteId.setText("Numer trasy: " + String.valueOf(route.getRouteId()));

        TextView textRouteName = holder.textRouteName;
        textRouteName.setText("Nazwa trasy: " + route.getRouteName());

        //  TextView textStats = holder.txtStats;
        //  textStats.setText("Stats: " + countPoint(route.getRouteId(), DELETE_DESCRIPTION, ));

        if (!isSelectedAll) holder.showCheckBox.setChecked(false);
        else holder.showCheckBox.setChecked(true);

        final CheckBox checkBox = holder.showCheckBox;
        int actualRouteId = mData.get(position).getRouteId();

        TextView textDate = holder.textDate;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
        String stringDate = simpleDateFormat.format(route.getDate());
        textDate.setText("Data: " + stringDate);


        //ustawienie checkboxa w pozycji ustawionej przez użytkownika (MapsActivity.routeToDraw)
        checkBox.setChecked(false);
        for (Integer i : MapsActivity.getRoutesToDraw()) {
            if (i == actualRouteId) {
                checkBox.setChecked(true);
            }
        }


        holder.showCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            //kliknięcie checkboxa "pokaż na mapie"
            public void onClick(View view) {
                //odznaczanie checkboxa select all
                SearchRouteActivity.checkBoxSelectAll.setChecked(false);
                isSelectedAll = false;
                Integer actualRouteId = mData.get(position).getRouteId();
                if (checkBox.isChecked()) {
                    MapsActivity.getRoutesToDraw().add(actualRouteId);
                    //sprawdzenie czy wszystkie checkboxy są zaznaczone i ustawienie checkbox all jeśli są
                    if(mData.size() == MapsActivity.getRoutesToDraw().size()){
                        selectAll(true);
                        SearchRouteActivity.checkBoxSelectAll.setChecked(true);
                    }
                } else {
                    int tmp = MapsActivity.getRoutesToDraw().indexOf(actualRouteId);
                    MapsActivity.getRoutesToDraw().remove(tmp);
                }
                Toast.makeText(view.getContext(), "Route: ", Toast.LENGTH_SHORT).show();

            }
        });


        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            //kliknięcie na button delete
            public void onClick(View view) {
                //usunięcie route z bazy
                if (mData.size() != 0) {
                    int routeId = mData.get(position).getRouteId();
                    delteRoute(mData.get(position), view.getContext());
                    deltePoint(routeId, DELETE_DESCRIPTION, view.getContext());
                    deltePoint(routeId, DELETE_LOCATION_POINT, view.getContext());
                    deltePoint(routeId, DELETE_PHOTO, view.getContext());
                    deltePoint(routeId, DELETE_VOICE, view.getContext());

                    mData.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mData.size());
                }
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textRouteId;
        TextView textRouteName;
        CheckBox showCheckBox;
        Button btnDelete;
        TextView textDate;
        TextView txtStats;

        ViewHolder(View itemView) {
            super(itemView);
            textRouteId = itemView.findViewById(R.id.route_id);
            textRouteName = itemView.findViewById(R.id.route_name);
            textDate = itemView.findViewById(R.id.txt_date);
            txtStats = itemView.findViewById(R.id.txt_stats);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Route getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    //skasowanie pojedynczej trasy z bazy danych w oddzielnym wątku
    public void delteRoute(Route route, final Context context) {

        class DeleteRoute extends AsyncTask<Route, Void, Void> {

            @Override
            protected Void doInBackground(Route... routes) {
                //odczytanie danych z bazy
                DatabaseClient.getInstance(context)
                        .getAppDatabase()
                        .routeDao()
                        .delete(routes[0]);
                return null;
            }

        }

        DeleteRoute dr = new DeleteRoute();
        dr.execute(route);
    }

    //skasowanie punktu (LocationPoint, Message, VoiceMessage lub Photo) z bazy danych w oddzielnym wątku
    public void deltePoint(int routeId, final int objectToDelete, final Context context) {

        class DeletePoint extends AsyncTask<Integer, Void, Void> {

            @Override
            protected Void doInBackground(Integer... routeId) {
                //odczytanie danych z bazy
                if (objectToDelete == DELETE_LOCATION_POINT) {
                    DatabaseClient.getInstance(context)
                            .getAppDatabase()
                            .locationDao()
                            .delete(routeId[0]);
                    return null;
                }
                if (objectToDelete == DELETE_DESCRIPTION) {
                    DatabaseClient.getInstance(context)
                            .getAppDatabase()
                            .userDao()
                            .delete(routeId[0]);
                    return null;
                }
                if (objectToDelete == DELETE_PHOTO) {
                    DatabaseClient.getInstance(context)
                            .getAppDatabase()
                            .photoDao()
                            .delete(routeId[0]);
                    return null;
                }
                if (objectToDelete == DELETE_VOICE) {
                    DatabaseClient.getInstance(context)
                            .getAppDatabase()
                            .voiceMessageDao()
                            .delete(routeId[0]);
                    return null;
                }

                return null;
            }

        }

        DeletePoint dp = new DeletePoint();
        dp.execute(routeId);
    }

    public void selectAll(boolean select) {
        isSelectedAll = select;
        notifyDataSetChanged();
    }
    
/*
    //policzenie punktów (LocationPoint, Message, VoiceMessage lub Photo) z bazy danych w oddzielnym wątku
    public Integer countPoint(int routeId, final int objectToCount, final Context context) {

        class CountPoint extends AsyncTask<Integer, Void, Integer> {

            @Override
            protected Integer doInBackground(Integer... routeId) {
                //odczytanie danych z bazy
                int count = 0;
                if (objectToCount == DELETE_LOCATION_POINT) {
                    count = DatabaseClient.getInstance(context)
                            .getAppDatabase()
                            .locationDao()
                            .count(routeId[0]);
                    return count;
                }
                if (objectToCount == DELETE_DESCRIPTION) {
                    count = DatabaseClient.getInstance(context)
                            .getAppDatabase()
                            .userDao()
                            .count(routeId[0]);
                    return count;
                }
                if (objectToCount == DELETE_PHOTO) {
                    count = DatabaseClient.getInstance(context)
                            .getAppDatabase()
                            .photoDao()
                            .count(routeId[0]);
                    return count;
                }
                if (objectToCount == DELETE_VOICE) {
                    count = DatabaseClient.getInstance(context)
                            .getAppDatabase()
                            .voiceMessageDao()
                            .count(routeId[0]);
                    return count;
                }

                return count;
            }

        }

        Integer count = 0;
        CountPoint cp = new CountPoint();
        cp.execute(routeId);
        try {
            count = cp.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return count;
    }
*/
}