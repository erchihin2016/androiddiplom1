package com.example.androiddiplom.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androiddiplom.App;
import com.example.androiddiplom.Executor;
import com.example.androiddiplom.Note;
import com.example.androiddiplom.R;
import com.example.androiddiplom.adapter.NotesDataAdapter;
import com.example.androiddiplom.database.NoteDao;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.example.androiddiplom.R.string.toast_edit_note_click;

public class MainActivity extends AppCompatActivity {
    private NotesDataAdapter adapter;
    private EditText search;
    private NoteDao noteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteDao = App.getComponent().getStorage().getNoteDao();
        getAllNotes();

        search = findViewById(R.id.edTxt_search);
        FloatingActionButton fabAdd = findViewById(R.id.fabConfirm);
        BottomAppBar barMain = findViewById(R.id.barEditor);
        setSupportActionBar(barMain);

        ListView listView = findViewById(R.id.listView);

        adapter = new NotesDataAdapter(this, null);
        listView.setAdapter(adapter);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEditor = new Intent(MainActivity.this, EditorActivity.class);
                intentEditor.removeExtra("noteIndex");
                startActivity(intentEditor);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentEditor = new Intent(MainActivity.this, EditorActivity.class);
                int noteIndex = adapter.getItem(position).getNoteId();
                intentEditor.putExtra("noteIndex", noteIndex);
                startActivity(intentEditor);
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, toast_edit_note_click, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllNotes() {
        Executor.IOThread(new Runnable() {
            @Override
            public void run() {
                final List<Note> notes = noteDao.getAllNote();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addAllNotes(notes);
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_search:
                search.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                return true;
            case R.id.menu_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_navigation_menu_main, menu);
        return true;
    }
}