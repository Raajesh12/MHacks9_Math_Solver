package com.example.raajesharunachalam.mathsolver;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import com.example.raajesharunachalam.mathsolver.data.APIKey;
import com.example.raajesharunachalam.mathsolver.data.Converter;
import com.example.raajesharunachalam.mathsolver.data.Subpod;
import com.example.raajesharunachalam.mathsolver.data.WolframAdapter;
import com.example.raajesharunachalam.mathsolver.data.WolframAlphaAsyncTask;

import java.util.ArrayList;

public class SolutionActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://api.wolframalpha.com/v2/query?input=";
    private static final String FORMAT = "&format=image,plaintext";
    private static final String OUTPUT = "&output=JSON";
    private static final String APP_ID = "&appid=" + APIKey.ID;

    RecyclerView solutionList;
    ArrayList<Subpod> subpods;
    WolframAdapter adapter;
    String result;

    ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution);

        Intent startedSolutionActivity = getIntent();
        if(startedSolutionActivity.hasExtra(MainActivity.TO_SOLUTIONS_LABEL) && startedSolutionActivity.hasExtra(MainActivity.INPUT_LABEL)){
            Bundle bundle = startedSolutionActivity.getExtras();
            result = bundle.getString(MainActivity.INPUT_LABEL);
            Log.e("RESULT", result);
        }

        solutionList = (RecyclerView) findViewById(R.id.solution_list);

        subpods = new ArrayList<>();

        adapter = new WolframAdapter(this, subpods);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        solutionList.setAdapter(adapter);
        solutionList.setLayoutManager(layoutManager);

        WolframAlphaAsyncTask wolframTask = new WolframAlphaAsyncTask(adapter, getApplicationContext());
        String input = Converter.convertMathToWolfram(result);
        input = input.trim();
        String urlString = createFullURL(input);
        Log.e("URL:", urlString);
        wolframTask.execute(urlString);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate share_menu resource file.
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int id = item.getItemId();
        if(id == R.id.menu_item_email){
            StringBuilder linksBuilder = new StringBuilder();
            for(Subpod subpod : adapter.getResults()){
                if(subpod.getImage() != null) {
                    linksBuilder.append(subpod.getImage().getSourceLink());
                    linksBuilder.append("\n");
                    linksBuilder.append("\n");
                }
            }
            String links = linksBuilder.toString();
            Log.e("Links", links);
            shareText(links);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareText(String textToShare) {
        Log.e("Links", textToShare);
        String mimeType = "text/plain";
        String title = "Share Solutions Image Links";

        ShareCompat.IntentBuilder
                .from(this)
                .setType(mimeType)
                .setChooserTitle(title)
                .setText(textToShare)
                .startChooser();
    }


    public String createFullURL(String input) {
        String fullURL = BASE_URL + input + FORMAT + OUTPUT + APP_ID;
        return fullURL;
    }
}