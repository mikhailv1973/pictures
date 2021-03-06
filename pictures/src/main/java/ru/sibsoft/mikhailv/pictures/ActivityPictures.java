package ru.sibsoft.mikhailv.pictures;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Arrays;

public class ActivityPictures extends AppCompatActivity {

    Bookshelf bookshelf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

        bookshelf = (Bookshelf)getSupportFragmentManager().findFragmentByTag("BOOKSHELF");
        if(bookshelf == null) {
            bookshelf = new Bookshelf();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(bookshelf, "BOOKSHELF")
                    .commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.exit).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                drawer.closeDrawer(GravityCompat.START);
                finish();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_pictures, menu);
        for(Pair i: Arrays.asList(
                Pair.create(R.id.action_settings, new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(ActivityPictures.this, ActivitySettings.class);
                        startActivity(intent);
                        return true;
                    }
                }),
                Pair.create(R.id.action_add_to_favorites, new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return true;
                    }
                }),
                Pair.create(R.id.action_remove_from_favorites, new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return true;
                    }
                }))) {
            menu.findItem((Integer)i.first).setOnMenuItemClickListener((MenuItem.OnMenuItemClickListener)i.second);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_add_to_favorites).setVisible(!getFragment().isVisiblePageFavourite());
        menu.findItem(R.id.action_add_to_favorites).setEnabled(!getFragment().isSlideshow());
        menu.findItem(R.id.action_remove_from_favorites).setVisible(getFragment().isVisiblePageFavourite());
        menu.findItem(R.id.action_remove_from_favorites).setEnabled(!getFragment().isSlideshow());
        return super.onPrepareOptionsMenu(menu);
    }

    public Bookshelf getBookshelf() {
        return bookshelf;
    }

    public FragmentPictures getFragment() {
        return (FragmentPictures)getSupportFragmentManager().findFragmentById(R.id.fragment_pictures);
    }
}
