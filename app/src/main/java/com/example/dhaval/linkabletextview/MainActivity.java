package com.example.dhaval.linkabletextview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dhaval.linkabletextview.Library.Link;
import com.example.dhaval.linkabletextview.Library.LinkableEditText;
import com.example.dhaval.linkabletextview.Library.LinkableTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    ArrayList<String> country = new ArrayList<>();

    List<Link> links;
    int startPos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        country.add("Belgium");
        country.add("France");
        country.add("Italy");
        country.add("Germany");
        country.add("Spain");


        Link linkHashtag = new Link(Pattern.compile("(#\\w+)"))
                .setUnderlined(true)
                .setTextStyle(Link.TextStyle.ITALIC)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                    }
                });

        Link linkUsername = new Link(Pattern.compile("(@\\w+)"))
                .setUnderlined(false)
                .setTextColor(Color.parseColor("#D00000"))
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                    }
                });


        links = new ArrayList<>();
        links.add(linkHashtag);
        links.add(linkUsername);

        final LinkableTextView textView = (LinkableTextView) findViewById(R.id.textView);
        final Button submitButton = (Button) findViewById(R.id.submitButton);
        final LinkableEditText editText = (LinkableEditText) findViewById(R.id.editText);
        final TextView foundLinksView = (TextView) findViewById(R.id.foundLinks);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, country);


        editText.setTokenizer(new SpaceTokenizer());
        editText.setAdapter(adapter);

        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

         /*       textView.setText(String.valueOf(parent.getItemAtPosition(position)))
                        .addLinks(links)
                        .build();
*/

                List<Link> foundLinks = editText.getFoundLinks();

                int i = 0;
                int size = foundLinks.size();
                String found = "";
                while (i < size) {
                    found += foundLinks.get(i).getText();
                    found += i < size - 1 ? ", " : "";
                    i++;
                }


                String searchText = "@" + parent.getItemAtPosition(position).toString();
                StringBuffer text = new StringBuffer(editText.getText().toString());
                editText.setText(text);

//                text.replace(startPos, startPos + searchText.length(), searchText);

//                String finalString = text.substring(0, startPos) + searchText + text.substring(startPos + 1, text.length());

//                String temp = "@" + parent.getItemAtPosition(position).toString();
//                Log.e("after", finalString);

//                editText.setSelection(startPos + 1 + searchText.length() + 1);

                editText.requestFocus();
                int visible = size > 0 ? View.VISIBLE : View.GONE;
                foundLinksView.setVisibility(visible);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //textView.setText(editText.getText().toString()).build();
                textView.setText(editText.getText().toString())
                        .addLinks(links)
                        .build();
            }
        });


        editText.addLinks(links);
        editText.setTextChangedListener(new LinkableEditText.OnTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int lengthBefore, int lengthAfter) {

                if (lengthBefore == 0) {
                    start--;
                } else if (lengthBefore > 1) {
                    start += (lengthBefore - 1);
                }

                if (s != null && s.length() > 0) {
                    String text = s.toString();

                    if (lengthBefore > lengthAfter) {
                        text = text + "1";
                    }
                    for (int i = start; i >= 0; i--) {
                        char c = text.charAt(i);

                        if (c == ' ') {
                            break;
                        } else if (c == '@' || c == '#') {
                            if (i == 0 || text.charAt(i - 1) == ' ' || text.charAt(i - 1) == '\n') {

                                startPos = i;

                                //Call your webservice hear

                                Log.e("startpos", String.valueOf(startPos));

                                Log.e("search val", String.valueOf(editText.getText().subSequence(startPos + 1, editText.length())));
                                break;
                            } else {
                                break;
                            }
                        }
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                submitButton.setEnabled(s.length() > 0);

                List<Link> foundLinks = editText.getFoundLinks();

                int i = 0;
                int size = foundLinks.size();
                String found = "";
                while (i < size) {
                    found += foundLinks.get(i).getText();
                    found += i < size - 1 ? ", " : "";
                    i++;
                }
                foundLinksView.setText("Found: " + found);

                int visible = size > 0 ? View.VISIBLE : View.GONE;
                foundLinksView.setVisibility(visible);
            }
        });


    }
}
