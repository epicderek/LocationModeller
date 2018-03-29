package domain.appdevelopment.derek.locationmodeller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayTable extends AppCompatActivity
{
    /**
     * The view that displays the current information.
     */
    private TextView display;
    /**
     * The button that returns to the previous collection site.
     */
    private Button back;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_table);
        String dis = getIntent().getExtras().getString("table");
        display = findViewById(R.id.display);
        display.setMovementMethod(new ScrollingMovementMethod());
        //Append the text.
        display.append(dis);
        //Set up the button for proper return.
        back = findViewById(R.id.back);
        back.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View view)
            {
                DisplayTable.this.finish();
            }
        });
    }
}
