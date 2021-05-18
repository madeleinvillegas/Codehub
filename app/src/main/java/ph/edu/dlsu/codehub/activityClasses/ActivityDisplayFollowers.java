package ph.edu.dlsu.codehub.activityClasses;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ph.edu.dlsu.codehub.R;
import ph.edu.dlsu.codehub.activityClasses.EditPostActivity;

public class ActivityDisplayFollowers extends AppCompatActivity{

    private RecyclerView followerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        followerList = (RecyclerView) findViewById(R.id.recyclerView);
        followerList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        followerList.setLayoutManager(linearLayoutManager);

        setContentView(R.layout.activity_followers_list);
    }


    @Override
    public void onStart() {
        super.onStart();
    }


    public static void showMenu(@NonNull View itemView, String pos, String title, String body) {
        PopupMenu popupMenu = new PopupMenu(itemView.getContext(), itemView);
        popupMenu.inflate(R.menu.triple_dots_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu1:
                        Intent intent = new Intent(itemView.getContext(), EditPostActivity.class);
                        intent.putExtra("pos", pos);
                        intent.putExtra("title", title);
                        intent.putExtra("body", body);
                        itemView.getContext().startActivity(intent);
                        return true;
                    case R.id.menu2:
                        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(pos);
                        postRef.removeValue();
                        Toast.makeText(itemView.getContext(), "Successfully deleted the post", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }


}
