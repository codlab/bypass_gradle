package eu.codlab.markdown;

import android.content.Context;
import android.util.Log;

import eu.codlab.markdown.entities.MarkDownEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by kevinleperf on 08/01/15.
 */
public class Markdown {
    private Context _context;

    private Markdown() {

    }

    public Markdown(Context context) {
        _context = context;
    }

    public List<MarkDownEntity> processText(String text) {
        try{
            SplitManager manager = new SplitManager();
            manager.setup(text);
            List<MarkDownEntity> entities = manager.compute();
            return entities;
        }catch(Exception e){
            Log.d("Markdown", "having exception while reading data " + text);
            Log.d("Markdown", ""+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<MarkDownEntity> processAssetFile(String file_path) {
        try {
            InputStream is = _context.getAssets().open(file_path);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();
            String data_read = new String(bytes, "UTF-8");

            return processText(data_read);
        } catch (IOException exception) {
            Log.d("Markdown", "having exception while reading asset " + file_path);
            Log.d("Markdown", ""+exception.getMessage());
            exception.printStackTrace();
        }
        return null;
    }


}
