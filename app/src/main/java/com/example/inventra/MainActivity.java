package com.example.inventra;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.admin.AboutUsFragment;
import com.example.inventra.admin.AddCategory;
import com.example.inventra.admin.AddProduct;
import com.example.inventra.admin.Cart;
import com.example.inventra.admin.HomeFragment;
import com.example.inventra.admin.ManageStaff;
import com.example.inventra.admin.ProductFragment;
import com.example.inventra.admin.add_inventory;
import com.example.inventra.admin.billing_history;
import com.example.inventra.admin.search;
import com.example.inventra.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.bumptech.glide.Glide;
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;
    private ActivityMainBinding binding;
    private boolean isAdmin;
    ShapeableImageView user_profile, userSidebarView;
    LinearLayout navigateToProfile;

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Set status bar color and icon visibility
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.white)); // Match toolbar color
        window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // Ensure dark icons

        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up DrawerLayout and NavigationView (Sidebar)
        drawerLayout = findViewById(R.id.drawer_layout);

        // Set up Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user_profile = findViewById(R.id.profile_image);
        navigateToProfile = findViewById(R.id.profileViews);
        navigateToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new Profile());
            }
        });

        // Set up ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        toggle.setDrawerIndicatorEnabled(false); // Disable default hamburger icon
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.getNavigationIcon().setTint(ContextCompat.getColor(this, R.color.black));

        // Handle navigation icon click to toggle drawer
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        // sidebar -----------------------------
        NavigationView navigationView = findViewById(R.id.nav_view);

        // sidebar name display
        View headerView = navigationView.getHeaderView(0);
        TextView userNameView = headerView.findViewById(R.id.usernameView);
        TextView userRoleView = headerView.findViewById(R.id.userRoleView);
        userSidebarView = headerView.findViewById(R.id.user_side_profile);

        SharedPreferences sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        int userId = sharedPref.getInt("UserId", -1);

        // for getting user's profile picture
        String profile_pic_name = sharedPref.getString("profilePicture",null);

        if (profile_pic_name != null && !profile_pic_name.isEmpty()) {
            String imageUrl = ApiClient.BASE_PROFILE_URL + profile_pic_name;
            Log.d("User Profile == ==== ",imageUrl);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_default) // Optional placeholder
                    .error(R.drawable.profile_default)       // Fallback image
                    .into(user_profile);

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_default) // Optional placeholder
                    .error(R.drawable.profile_default)       // Fallback image
                    .into(userSidebarView);
        } else {
            // Set default profile picture
            user_profile.setImageResource(R.drawable.profile_default);
        }

        isAdmin = false;
        String userRole = sharedPref.getString("UserType", "");
        String username2 = sharedPref.getString("UserName", "");
        boolean isLoggedIn = sharedPref.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            userNameView.setText(username2);
            userRoleView.setText(capitalizeFirstLetter(userRole));
            TextView greetingTextView = findViewById(R.id.greeting_text);
            if (greetingTextView != null) {
                greetingTextView.setText("HEY, " + username2.toUpperCase());
            }
        }

        if (userRole.trim().equals("admin")) {
            isAdmin = true;
        } else {
            isAdmin = false;
        }

        if (!isAdmin) {
            Menu menu = navigationView.getMenu();
            MenuItem staffManagementItem = menu.findItem(R.id.staff_management);
            staffManagementItem.setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.add_category_nav) {
                loadFragment(new AddCategory());
            } else if (id == R.id.add_product_nav) {
                loadFragment(new AddProduct());
            } else if (id == R.id.nav_about) {
                loadFragment(new AboutUsFragment());
            } else if (id == R.id.nav_logout) {
                showLogoutConfirmationDialog();
            } else if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
            } else if (id == R.id.add_inventory_nav) {
                loadFragment(new add_inventory());
            } else if (id == R.id.billin_history) {
                loadFragment(new billing_history());
            } else if (id == R.id.staff_management) {
                loadFragment(new ManageStaff());
            } else if (id == R.id.nav_profile) {
                loadFragment(new Profile());
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Bottom Navigation Listener
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    loadFragment(new HomeFragment());
                } else if (id == R.id.nav_products) {
                    loadFragment(new ProductFragment());
                } else if (id == R.id.nav_scan) {
                    loadFragment(new add_inventory());
                } else if (id == R.id.nav_search) {
                    loadFragment(new search());
                }
                return true;
            }
        });

        // Floating Action Button click listener
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> loadFragment(new Cart()));

        loadFragment(new HomeFragment());
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences preferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProfileImage();
    }

    public void updateProfileImage() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String imageName = sharedPreferences.getString("profilePicture", null);

        if (imageName != null && !imageName.isEmpty()) {
            String imageUrl = ApiClient.BASE_PROFILE_URL + imageName;

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.person_defualt_image)
                    .error(R.drawable.person_defualt_image)
                    .into(user_profile);

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.person_defualt_image)
                    .error(R.drawable.person_defualt_image)
                    .into(userSidebarView);
        } else {
            user_profile.setImageResource(R.drawable.person_defualt_image);
            userSidebarView.setImageResource(R.drawable.person_defualt_image);
        }
    }

}