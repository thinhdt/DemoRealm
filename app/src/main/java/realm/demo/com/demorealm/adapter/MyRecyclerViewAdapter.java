package realm.demo.com.demorealm.adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import realm.demo.com.demorealm.R;
import realm.demo.com.demorealm.activities.RecyclerViewExampleActivity;
import realm.demo.com.demorealm.models.Book;

public class MyRecyclerViewAdapter extends RealmRecyclerViewAdapter<Book, MyRecyclerViewAdapter.MyViewHolder> {

    private final RecyclerViewExampleActivity activity;
    private LayoutInflater inflater;
    private Realm realm;

    public MyRecyclerViewAdapter(RecyclerViewExampleActivity activity, OrderedRealmCollection<Book> data) {
        super(activity, data, true);
        this.activity = activity;
        realm = Realm.getDefaultInstance();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_books, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Book book = getData().get(position);
        holder.data = book;

        // set the title and the snippet
        holder.textTitle.setText(book.getTitle());
        holder.textAuthor.setText(book.getAuthor());
        holder.textDescription.setText(book.getDescription());

        // load the background image
        if (book.getImageUrl() != null) {
            Glide.with(context)
                    .load(book.getImageUrl().replace("https", "http"))
                    .asBitmap()
                    .fitCenter()
                    .into(holder.imageBackground);
        }

        //update single match from realm
        holder.card.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View content = inflater.inflate(R.layout.edit_item, null);
                final EditText editTitle = (EditText) content.findViewById(R.id.title);
                final EditText editAuthor = (EditText) content.findViewById(R.id.author);
                final EditText editThumbnail = (EditText) content.findViewById(R.id.thumbnail);

                editTitle.setText(book.getTitle());
                editAuthor.setText(book.getAuthor());
                editThumbnail.setText(book.getImageUrl());

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(content)
                        .setTitle("Edit Book")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                RealmResults<Book> results = realm.where(Book.class).equalTo(Book.ID, book.getId()).findAll();

                                realm.beginTransaction();
                                results.get(0).setAuthor(editAuthor.getText().toString());
                                results.get(0).setTitle(editTitle.getText().toString());
                                results.get(0).setImageUrl(editThumbnail.getText().toString());

                                realm.commitTransaction();

                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private CardView card;
        private TextView textTitle;
        private TextView textAuthor;
        private TextView textDescription;
        private ImageView imageBackground;
        private Book data;

        public MyViewHolder(View view) {
            super(view);

            card = (CardView) itemView.findViewById(R.id.card_books);
            textTitle = (TextView) itemView.findViewById(R.id.text_books_title);
            textAuthor = (TextView) itemView.findViewById(R.id.text_books_author);
            textDescription = (TextView) itemView.findViewById(R.id.text_books_description);
            imageBackground = (ImageView) itemView.findViewById(R.id.image_background);

            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            activity.deleteItem(data);
            return true;
        }
    }
}