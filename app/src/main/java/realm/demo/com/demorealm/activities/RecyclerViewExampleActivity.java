package realm.demo.com.demorealm.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

import io.realm.Realm;
import realm.demo.com.demorealm.R;
import realm.demo.com.demorealm.adapter.DividerItemDecoration;
import realm.demo.com.demorealm.adapter.MyRecyclerViewAdapter;
import realm.demo.com.demorealm.models.Book;
import realm.demo.com.demorealm.models.Prefs;

public class RecyclerViewExampleActivity extends AppCompatActivity {

    private Realm realm;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        realm = Realm.getDefaultInstance();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        setUpRecyclerView();

        if (!Prefs.with(this).getPreLoad()) {
            setRealmData();
            queryItem();
        } else {
            queryItem();
        }

        //add new item
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBook();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listview_options, menu);
        return true;
    }

    private void setRealmData() {

        ArrayList<Book> books = new ArrayList<>();

        Book book = new Book();
        book.setId(1 + System.currentTimeMillis());
        book.setAuthor("Reto Meier");
        book.setTitle("Android 4 Application Development");
        book.setImageUrl("http://api.androidhive.info/images/realm/1.png");
        books.add(book);

        book = new Book();
        book.setId(2 + System.currentTimeMillis());
        book.setAuthor("Itzik Ben-Gan");
        book.setTitle("Microsoft SQL Server 2012 T-SQL Fundamentals");
        book.setImageUrl("http://api.androidhive.info/images/realm/2.png");
        books.add(book);

        book = new Book();
        book.setId(3 + System.currentTimeMillis());
        book.setAuthor("Magnus Lie Hetland");
        book.setTitle("Beginning Python: From Novice To Professional Paperback");
        book.setImageUrl("http://api.androidhive.info/images/realm/3.png");
        books.add(book);

        book = new Book();
        book.setId(4 + System.currentTimeMillis());
        book.setAuthor("Chad Fowler");
        book.setTitle("The Passionate Programmer: Creating a Remarkable Career in Software Development");
        book.setImageUrl("http://api.androidhive.info/images/realm/4.png");
        books.add(book);

        book = new Book();
        book.setId(5 + System.currentTimeMillis());
        book.setAuthor("Yashavant Kanetkar");
        book.setTitle("Written Test Questions In C Programming");
        book.setImageUrl("http://api.androidhive.info/images/realm/5.png");
        books.add(book);


        for (final Book b : books) {
            // Persist your data easily
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealm(b);
                }
            });
        }

        Prefs.with(this).setPreLoad(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {

            addBook();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addBook() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Random ran = new Random();
                int x = ran.nextInt(10) + 1; // random 1 -> 10

                Book book = new Book();
                book.setId(System.currentTimeMillis());
                book.setAuthor("New Book " + x);
                book.setTitle("Written Test Questions In C Programming");
                book.setImageUrl("http://api.androidhive.info/images/realm/5.png");

                realm.copyToRealm(book);
            }
        });
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyRecyclerViewAdapter(this, realm.where(Book.class).findAllAsync()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    public void deleteItem(final Book item) {
        final long itemId = item.getId();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(RecyclerViewExampleActivity.this);
        builder1.setMessage("Are you sure want to delete?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.where(Book.class).equalTo(Book.ID, itemId)
                                        .findAll()
                                        .deleteAllFromRealm();
                            }
                        });

                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
    }

    private void queryItem() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Book.class)
                        .findAll();
            }
        });
    }
}