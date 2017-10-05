package tm.fantom.doittesttask.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import tm.fantom.doittesttask.R;

public class MainActivity extends AppCompatActivity implements MainFragment.Listener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, MainFragment.newInstance())
                    .commit();
        }
    }

    @Override public void onUploadClicked(String token) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,
                        R.anim.slide_out_right)
                .replace(android.R.id.content, UploadFragment.newInstance(token))
                .addToBackStack(null)
                .commit();
    }

    @Override public void onPlayClicked(String token) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,
                        R.anim.slide_out_right)
                .replace(android.R.id.content, GifFragment.newInstance(token))
                .addToBackStack(null)
                .commit();
    }
}
