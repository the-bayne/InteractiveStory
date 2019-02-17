package com.baynecorp.interactivestory.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baynecorp.interactivestory.R;
import com.baynecorp.interactivestory.model.Page;
import com.baynecorp.interactivestory.model.Story;

import java.util.Stack;

public class StoryActivity extends AppCompatActivity {

    public static final String TAG = StoryActivity.class.getSimpleName();

    private Story story;
    private ImageView storyImageView;
    private TextView storyTextView;
    private Button choice1Button;
    private Button choice2Button;

    private String name;

    private Stack<Integer> pageStack = new Stack<Integer>();

    public StoryActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        storyImageView = findViewById(R.id.storyImageView);
        storyTextView = findViewById(R.id.storyTextView);
        choice1Button = findViewById(R.id.choice1Button);
        choice2Button = findViewById(R.id.choice2Button);

        Intent intent = getIntent();
        name = intent.getStringExtra(getString(R.string.key_name));
        if (name == null || name.isEmpty()) {
            name = "Jared";
        }

        story = new Story();
        loadPage(0);
    }

    @Override
    public void onBackPressed() {
        // Going back to a previous page
        pageStack.pop();
        if (pageStack.isEmpty()) {
            super.onBackPressed();
        } else {
            loadPage(pageStack.pop());
        }
    }

    private void loadPage(int pageNumber) {
        // Pushes the pageNumber onto the stack
        pageStack.push(pageNumber);
        final Page page = story.getPage(pageNumber);

        Drawable image = ContextCompat.getDrawable(this, page.getImageId());
        storyImageView.setImageDrawable(image);

        String pageText = getString(page.getTextId());
        // Add name if placeholder included, and ignore if not
        pageText = String.format(pageText, name);
        // If isFinalPage() then give the user only one choice to return to first screen
        if (page.isFinalPage()) {
            choice1Button.setVisibility(View.INVISIBLE);
            choice2Button.setText(R.string.playAgain);
            choice2Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //finish();
                    loadPage(0);
                }
            });
        } else {
            loadButtons(page, pageText);
        }
    }

    private void loadButtons(final Page page, String pageText) {
        // If you click on Play Again then it still has the button visibility attributes from the last page, so we need to show the buttons each time they load
        choice1Button.setVisibility(View.VISIBLE);
        choice2Button.setVisibility(View.VISIBLE);
        storyTextView.setText(pageText);
        choice1Button.setText(page.getChoice1().getTextId());
        choice1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextPage = page.getChoice1().getNextPage();
                loadPage(nextPage);
            }
        });

        choice2Button.setText(page.getChoice2().getTextId());
        choice2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextPage = page.getChoice2().getNextPage();
                loadPage(nextPage);
            }
        });
    }
}
