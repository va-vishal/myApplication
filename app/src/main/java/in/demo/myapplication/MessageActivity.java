package in.demo.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import in.demo.myapplication.Adapter.TabsAccessorAdapter;
import in.demo.myapplication.Fragments.AllFragment;
import in.demo.myapplication.Fragments.UsersFragment;

public class MessageActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ImageView back;
    private Button allButton, matchedUsersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initializeUI();
        setupViewPager();
        setupButtonListeners();
        setDefaultTab();

        back.setOnClickListener(v -> {
            startActivity(new Intent(MessageActivity.this, HomeActivity.class));
            finish();
        });

    }

    private void initializeUI() {
        viewPager = findViewById(R.id.view_pager);
        allButton = findViewById(R.id.all_button);
        back=findViewById(R.id.back);
        matchedUsersButton = findViewById(R.id.matched_users_button);
    }

    private void setupViewPager() {
        TabsAccessorAdapter adapter = new TabsAccessorAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllFragment(), "All");
        adapter.addFragment(new UsersFragment(), "Matched Users");

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateTabSelection(position);
            }
        });
    }

    private void setupButtonListeners() {
        allButton.setOnClickListener(v -> {
            viewPager.setCurrentItem(0);
            updateTabSelection(0);
        });
        matchedUsersButton.setOnClickListener(v -> {
            viewPager.setCurrentItem(1);
            updateTabSelection(1);
        });
    }

    private void setDefaultTab() {
        viewPager.setCurrentItem(0); // Default to the first tab
        updateTabSelection(0); // Ensure the default tab is selected
    }

    private void updateTabSelection(int position) {
        boolean isAllSelected = position == 0;
        boolean isMatchedUsersSelected = position == 1;

        // Set selected state
        allButton.setSelected(isAllSelected);
        matchedUsersButton.setSelected(isMatchedUsersSelected);

        // Update text color
        allButton.setTextColor(isAllSelected ? getColor(R.color.tab_text_selected) : getColor(R.color.tab_text_unselected));
        matchedUsersButton.setTextColor(isMatchedUsersSelected ? getColor(R.color.tab_text_selected) : getColor(R.color.tab_text_unselected));

        // Update background
        allButton.setBackgroundResource(isAllSelected ? R.drawable.rounded_button_background_selected : R.drawable.rounded_button_background);
        matchedUsersButton.setBackgroundResource(isMatchedUsersSelected ? R.drawable.rounded_button_background_selected : R.drawable.rounded_button_background);
    }
}
