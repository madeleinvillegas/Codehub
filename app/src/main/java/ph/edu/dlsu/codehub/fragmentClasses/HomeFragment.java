package ph.edu.dlsu.codehub.fragmentClasses;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ph.edu.dlsu.codehub.CommentActivity;
import ph.edu.dlsu.codehub.EditPostActivity;
import ph.edu.dlsu.codehub.ReportPostActivity;
import ph.edu.dlsu.codehub.Post;
import ph.edu.dlsu.codehub.R;


public class HomeFragment extends Fragment {
    private DatabaseReference postRef, likesRef;
    private RecyclerView postList;
    private Boolean isLiked = false;
    private String userId;
    private String TAG = "DEBUGGING_TAG";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        postList = view.findViewById(R.id.recyclerView);
        postList.setHasFixedSize(true);
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        viewPosts();
    }

    private void viewPosts() {
        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>().setQuery(postRef, Post.class).build();
        FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Post model) {
                String pos = getRef(position).getKey();
                String[] arr = pos.split(" ");
                String uidOfThePostAuthor = arr[0];

                holder.postDetails.setText(model.getFullName() + " | " + model.getDate() + " | " + model.getTime());
                holder.postBody.setText(model.getBody());
                holder.postTitle.setText(model.getTitle());
                holder.setLikeBtnColor(pos);

                if (uidOfThePostAuthor.equals(userId)) {
                    holder.reportBtn.setVisibility(View.INVISIBLE);
                    holder.optionsBtn.setVisibility(View.VISIBLE);
                }
                else {
                    holder.optionsBtn.setVisibility(View.INVISIBLE);
                    holder.reportBtn.setVisibility(View.VISIBLE);
                }

                holder.commentBtn.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(), CommentActivity.class);
                    intent.putExtra("Position", pos);
                    startActivity(intent);
                });




                holder.reportBtn.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(), ReportPostActivity.class);
                    intent.putExtra("postId", pos);
                    startActivity(intent);
                });

                holder.optionsBtn.setOnClickListener(view -> {
                    PostViewHolder.showMenu(view, pos, model.getTitle(), model.getBody());
                });

                holder.likeBtn.setOnClickListener(view -> {
                    isLiked = true;
                    likesRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            // User already liked the post so the post will be unliked
                            if(isLiked) {
                                if (snapshot.child(pos).hasChild(userId)) {
                                    likesRef.child(pos).child(userId).removeValue();
                                    isLiked = false;
                                }
                                // Post will be liked
                                else {
                                    likesRef.child(pos).child(userId).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //put code to display on notification on like here
                                            DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
//                                            notificationRef.child(userId).child()

                                        }
                                    });
                                    // Stops the infinite loop
                                    isLiked = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                });
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
                return new PostViewHolder(view);
            }
        };
        postList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }


    public static class PostViewHolder extends RecyclerView.ViewHolder {
        private ImageButton likeBtn, commentBtn, optionsBtn, reportBtn;
        private TextView noOfLikes, postDetails, postBody, postTitle;
        private int numberOfLikes;
        private String userId;
        private DatabaseReference likesRef;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postDetails = itemView.findViewById(R.id.single_post_details);
            postBody = itemView.findViewById(R.id.single_post_body);
            postTitle = itemView.findViewById(R.id.single_post_title);
            likeBtn = itemView.findViewById(R.id.like_btn);
            commentBtn = itemView.findViewById(R.id.comment_btn);
            reportBtn = itemView.findViewById(R.id.report_btn);
            noOfLikes = itemView.findViewById(R.id.number_of_likes);
            likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            optionsBtn = itemView.findViewById(R.id.options_btn);
        }


        public static void showMenu(@NonNull View itemView, String pos, String title, String body) {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), itemView);
            popupMenu.inflate(R.menu.triple_dots_menu);
            // TODO: Add edit and delete post stuff
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
        public void setLikeBtnColor(String pos) {
            likesRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                    if(snapshot.child(pos).hasChild(userId)) {
                        likeBtn.setImageResource(R.drawable.like);
                        numberOfLikes = (int) snapshot.child(pos).getChildrenCount();
                        noOfLikes.setText(numberOfLikes + " likes");
                    }
                    else {
                        likeBtn.setImageResource(R.drawable.unlike);
                        numberOfLikes = (int) snapshot.child(pos).getChildrenCount();
                        noOfLikes.setText(numberOfLikes + " likes");
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }

}
