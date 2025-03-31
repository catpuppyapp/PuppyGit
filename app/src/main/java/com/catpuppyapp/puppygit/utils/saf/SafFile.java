package com.catpuppyapp.puppygit.utils.saf;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import androidx.documentfile.provider.DocumentFile;


/**
 * Android write file in extsd card by Storage Access Framework(SAF)
 * This warppper can be used in both java and native (by hook)
 */
public class SafFile {

    public static final String LOGTAG="#YURIJAVA";
    public static Context g_context=null;
    public static SharedPreferences g_share=null;
    public static String g_spname="yuri"; //shared preference name
    
    /**
     * Use for regist jni file hook with SAF, such as fopen
     * @param hooksoStr, regexp str passing to xhook register 
     * @param soPath, dlopen this path before xhook register
     */
    public native static int nativeHookFile(String hooksoStr, String soPath);

    /**
     * Init java callbacks for jni hook
     */
    public native static void nativeInitSafJavaCallbacks(); 
//    public native static String getcwd();

    /**
     * 
     * @param context
     * @param share
     */
    public static void initValues(final Context context, final SharedPreferences share) {
        g_context = context;
        g_share = share;
    }

    /**
     * 
     * @param context
     * @param spname, the name of shared preference which contains docUri  
     */
    public static void initValues(final Context context, String spname) {
        if (spname == null)
            spname = "yuri";
        g_spname = spname;
        if (context != null) {
            final SharedPreferences share = context.getSharedPreferences(spname, Context.MODE_PRIVATE);
            initValues(context, share);
        }
    }

    /**
     * get file descripter for fdopen
     * @param curdir
     * @param filename
     * @param mode, used for fdopen
     * @return
     */
    public static int getFD(final String curdir, final String filename, final int mode) {

        if (filename == null || filename.length()==0)
            return 0;
        String fullpath = null;
        if (curdir != null && filename.charAt(0) != '/') {
            if (curdir.charAt(curdir.length() - 1) == ('/') || curdir.charAt(curdir.length() - 1) == ('\\'))
                fullpath = curdir + filename;
            else
                fullpath = curdir + "/" + filename;
        } else
            fullpath = filename;
        fullpath = fullpath.replace('\\', '/');
        
        int fd = 0;
        //Log.i(LOGTAG, "SafFile.getFD " + fullpath);
        try {
            if (mode == 0) { // only read, this is wrong because it can be chdir, not the path
                ParcelFileDescriptor pfd;
                pfd = ParcelFileDescriptor.open(new File(fullpath), ParcelFileDescriptor.MODE_READ_ONLY);
                fd = pfd.detachFd();
            } else if (Build.VERSION.SDK_INT >= 21) {
                DocumentFile base = getBaseDocumentFile(g_context, g_share);
                if (base == null) {
                    fixGValues();
                    base = getBaseDocumentFile(g_context, g_share);
                    if (base == null) {
                        Log.e(LOGTAG, "SafFile.getFD, base is null!");
                        return 0;
                    }
                }
                fd = getFdSaf(g_context, base, fullpath, "w");

            } else {
                ParcelFileDescriptor pfd;
                pfd = ParcelFileDescriptor.open(new File(fullpath), ParcelFileDescriptor.MODE_WRITE_ONLY);
                fd = pfd.detachFd();
            }
        } catch (final Exception e) {
            Log.e("#YURIJAVA", "getFD error:  " + e.getClass().getName());
        }
        return fd;
    }

    /**
     * 
     * @param curdir
     * @param filename
     * @return
     */
    public static int mkdir(final String curdir, final String filename, int mode) {
        Log.d(LOGTAG, "curdir="+curdir+", filename="+filename);
        if (filename == null || filename.length()==0)
            return 0;
        String fullpath = null;
        if (curdir != null && filename.charAt(0)!='/') {
            if (curdir.charAt(curdir.length() - 1) == ('/') || curdir.charAt(curdir.length() - 1) == ('\\'))
                fullpath = curdir + filename;
            else
                fullpath = curdir + "/" + filename;
        } else
            fullpath = filename;
        fullpath = fullpath.replace('\\', '/');

        //Log.i(LOGTAG, "SafFile.mkdir " + fullpath);
        File file= new File(fullpath);
        if(!file.mkdir()){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                DocumentFile base = getBaseDocumentFile(g_context, g_share);
                if (base == null) {
                    fixGValues();
                    base = getBaseDocumentFile(g_context, g_share);
                    if (base == null) {
                        Log.e(LOGTAG, "SafFile.mkdir, base is null!");
                        return 0;
                    }
                }
                if(mkdirsSaf(base, fullpath)!=null){
                    Log.i(LOGTAG, "SafFile.mkdir saf success!");
                    return 0;
                }
                else{
                    Log.e(LOGTAG, "SafFile.mkdir saf error!");
                    return 1;
                }
            }
            else return -1;
        }
        else{
            return 0;
        }
    }

    /**
     * 
     * @param curdir
     * @param filename
     * @return
     */
    public static int remove(final String curdir, final String filename) {
        if (filename == null || filename.length()==0)
            return 0;
        String fullpath = null;
        if (curdir != null && filename.charAt(0)!='/') {
            if (curdir.charAt(curdir.length() - 1) == ('/') || curdir.charAt(curdir.length() - 1) == ('\\'))
                fullpath = curdir + filename;
            else
                fullpath = curdir + "/" + filename;
        } else
            fullpath = filename;
        fullpath = fullpath.replace('\\', '/');

        //Log.i(LOGTAG, "SafFile.remove " + fullpath);
        File file= new File(fullpath);
        try{
            if(file.isDirectory() && file.listFiles().length >0){
                Log.e(LOGTAG, "SafFile.remove dir contains file");
                return -1;
            }
            boolean isdelete = file.delete();
            if(isdelete) return 0;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                DocumentFile base = getBaseDocumentFile(g_context, g_share);
                if (base == null) {
                    fixGValues();
                    base = getBaseDocumentFile(g_context, g_share);
                    if (base == null) {
                        Log.e(LOGTAG, "SafFile.remove, base is null!");
                        return 0;
                    }
                }
                if(deleteFileSaf(base, fullpath)){
                    //Log.i(LOGTAG, "SafFile.remove saf success!");
                    return 0;
                }
                else{
                    Log.e(LOGTAG, "SafFile.remove saf error!");
                    return 1;
                }
            }
            else return -1;
         } catch(Exception e){
            Log.e(LOGTAG, "SafFile.mkdir saf error! "+e.getClass().getName());
            return -1;
         }
    }

    /**
     * Use refection to get the application context
     * 
     * @return the Application context
     * @throws Exception
     */
    public static Context getApplicationContext() throws Exception {
        Application app = null;
        app = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null,
                (Object[]) null);
        if (app == null)
            app = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication")
                    .invoke(null, (Object[]) null);
        return app.getApplicationContext();
    }

    /**
     *
     * @return paths to all available external SD-Card roots in the system.
     */
    public static String[] getStorageDirectories(final Context context) {
        if (context == null)
        {
            Log.e(LOGTAG, "SafFile.getStorageDirections context is null!");
            return null;
        }
        
        String[] storageDirectories;
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Log.i(LOGTAG, "secondary_storage "+rawSecondaryStoragesStr);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final List<String> results = new ArrayList<String>();
            final File[] externalDirs = context.getExternalFilesDirs(null);
            if (externalDirs != null) {
                for (final File file : externalDirs) {
                    // Log.i(LOGTAG, "getExt...dirs " + file.getPath());
                    final String path = file.getPath().split("/Android")[0];
                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                            && Environment.isExternalStorageRemovable(file))
                            || rawSecondaryStoragesStr != null && rawSecondaryStoragesStr.contains(path)) {
                        results.add(path);
                    }
                }
            }
            storageDirectories = results.toArray(new String[0]); // toArray 模板
        } else {
            final Set<String> rv = new HashSet<String>();

            if (rawSecondaryStoragesStr != null && !TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                Collections.addAll(rv, rawSecondaryStorages);
            }
            storageDirectories = rv.toArray(new String[rv.size()]);
        }
        return storageDirectories;
    }

    public static void requestDocUri(final Activity activity, final int requestCode) {
        if(activity==null){
            Log.e(LOGTAG, "SafFile.requestDocUri activity is null");
            return;
        }
        final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        activity.startActivityForResult(intent, requestCode);
    }
    
    /**
     * 
     * @param context
     * @param share
     * @param intent
     * @param responseCode
     */
    public static void saveDocUriFromResult(final Context context, final SharedPreferences share, final Intent intent,
            final int responseCode) {
        if(context==null) {
            Log.e(LOGTAG, "SafFile.saveDocUriFromResult context is null!");
            return;
        }
        if(share==null){
            Log.e(LOGTAG, "SafFile.saveDocUriFromResult share is null!");
            return;
        } 
        if(intent==null){
            Log.e(LOGTAG, "SafFile.saveDocUriFromResult intent is null!");
            return;
        }
        
        final String p = share.getString("docUri", null);
        Uri oldUri = null;
        if (p != null)
            oldUri = Uri.parse(p);
        Uri treeUri = null;

        if (responseCode == Activity.RESULT_OK) { // Activity.RESULT_OK=-1
            treeUri = intent.getData();
            if (treeUri != null) {
                Log.i(LOGTAG, "Get treeUri successed!");
                share.edit().putString("docUri", treeUri.toString()).commit();
            } else {
                Log.e(LOGTAG, "Get treeUri failed!");
            }
        }
        if (responseCode != Activity.RESULT_OK) { // If not confirmed SAF, or if still not writable, then revert
                                                  // settings.
            if (treeUri != null)
                share.edit().putString("docUri", oldUri.toString()).commit();
            return;
        }
        // After confirmation, update stored value of folder.
        // Persist access permissions.
        final int takeFlags = intent.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        context.getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
    }

    /**
     * 
     * @param context
     * @param share, shared preference with docUri
     * @return extsdcard root DocumentFile
     */
    public static DocumentFile getBaseDocumentFile(final Context context, final SharedPreferences share) {
        if(context==null) {
            Log.e(LOGTAG, "SafFile.getBaseDocumentFile context is null!");
            return null;
        }
        if(share==null){
            Log.e(LOGTAG, "SafFile.getBaseDocumentFile share is null!");
            return null;
        }
        
        DocumentFile base = null;
        Uri docUri = null;
        final String p = share.getString("docUri", null);
        if (p != null) docUri = Uri.parse(p);
        Log.d(LOGTAG, "uriStr="+p+", base="+base);

        base = DocumentFile.fromTreeUri(context, docUri);
        return base;
    }

    /**
     * 
     * @param base, extsdcard DocumentFile
     * @param path, absolute path
     * @return the path's parent path DocumentFile
     */
    public static DocumentFile getTargetDirDocumentFile(final DocumentFile base, String path) {
        DocumentFile target = null;
        if (base == null) {
            Log.e(LOGTAG, "SafFile.getTargetDirDocumentFile base is null!");
            return null;
        }
        if(path==null) path="";

        path = path.replace("\\", "/");
        final String paths[] = path.split("/");
        int i;
        final int end = paths[paths.length - 1].length() > 0 ? paths.length - 1 : paths.length - 2;
        for (i = 0; i < end; i++) {
            // Log.i(LOGTAG, "getTar... path["+String.valueOf(i)+"], "+paths[i]);
            if (paths[i].equals(base.getName())) {
                if (i >= end - 1) {
                    // Log.i(LOGTAG, "getTar... "+path+" end="+paths[paths.length-1]+" "+ paths[end]);
                    return base;
                }
                i++;
                break;
            }
        }
        // Log.i(LOGTAG, "getTarget... "+base.getName()+" "+path);
        target = base.findFile(paths[i++]);
        // Log.i(LOGTAG, "target, "+ target.getName());
        for (; i < end; i++) {
            if (target == null)
                break;
            // Log.i(LOGTAG, "getTar..., "+path+" "+ target.getName());
            target = target.findFile(paths[i]);
        }
        return target;
    }
    
    /**
     * 
     * @param base, extsdcard DocumentFile
     * @param path, absolute path
     * @return
     */
    public static DocumentFile mkdirsSaf(final DocumentFile base, String path) {
        if(base==null){
            Log.e(LOGTAG, "SafFile.mkdirSaf base is null!");
            return null;
        }
        if(path==null) path="";
        
        DocumentFile df2 = null;
        path = path.replace("\\", "/");
        final String paths[] = path.split("/");
        final int end = paths[paths.length - 1].length() > 0 ? paths.length - 1 : paths.length - 2;
        int i;
        for (i = 0; i < end; i++) {
            if (paths[i].equals(base.getName())) {
                if (i >= end)
                    return base;
                i++;
                break;
            }
        }

        //Log.i(LOGTAG, "mkdirsSaf paths[end]="+paths[end]+" , "+paths[i]);
        df2 = base.findFile(paths[i++]);
        if(i > end){ // if create on the sdcard root
            if(df2!=null) return df2;
            else
                return base.createDirectory(paths[end]);
        }

        DocumentFile last = null;
        last = base;
        for (; i <= end; i++) // find existing dir
        {
            Log.i(LOGTAG, "mkdirSaf last "+last.getName()+" path["+String.valueOf(i)+"] "+paths[i]);
            if (df2 == null) {
                i--;
                break;
            }
            last = df2;
            df2 = df2.findFile(paths[i]);
        }
        if (df2 != null)
            return df2;
        df2 = last;
        for (; i <= end; i++) // create dir
        {
            df2 = df2.createDirectory(paths[i]);
            if (df2 == null) {
                Log.e(LOGTAG, "SafFile.mkdirsSaf failed at " + last.getName() + " " + paths[i]);
                break;
            }
        }
        return df2;
    }
    
    /**
     * 
     * @param base, extsdcard DocumentFile
     * @param path, absoluate path
     * @param append
     * @return
     */
    public static DocumentFile createFileSaf(final DocumentFile base, String path, final boolean append) {
        if(base==null){
            Log.e(LOGTAG, "SafFile.createFileSaf base is null!");
            return null;
        }
        if(path==null) path="";
        
        DocumentFile df2 = null;
        path = path.replace("\\", "/");
        final String paths[] = path.split("/");
        final DocumentFile target = getTargetDirDocumentFile(base, path);
        if (target != null) {
            df2 = target.findFile(paths[paths.length - 1]);
            if (df2 != null && df2.exists()) {
                if (!append)
                    df2.delete();
                else
                    return df2;
            }
            df2 = target.createFile("application/octet-stream", paths[paths.length - 1]);
        } else {
            Log.e(LOGTAG, "SafFile.createFileSaf target null, " + path + " error!");
        }
        return df2;
    }
    
    /**
     * 
     * @param base, extsdcard DocumentFile
     * @param path, absolute path
     * @return
     */
    public static boolean deleteFileSaf(final DocumentFile base, String path) {
        if(base==null){
            Log.e(LOGTAG, "SafFile.deleteFileSaf base is null!");
            return false;
        }
        if(path==null) path="";

        Boolean ret = false;
        DocumentFile df2 = null;
        path = path.replace("\\", "/");
        final String paths[] = path.split("/");
        final int end = paths[paths.length - 1].length() > 0 ? paths.length - 1 : paths.length - 2;
        final DocumentFile target = getTargetDirDocumentFile(base, path);
        if (target != null) {
            // Log.i(LOGTAG, "deleteFileaSaf target "+target.getName());
            df2 = target.findFile(paths[end]);
            if (df2 != null && df2.exists())
                ret = df2.delete();
        }
        // Log.i(LOGTAG, "deleteFileSaf ret=" + ret.toString()+ " path="+path);
        return ret;
    }

    /**
     * 
     * @param context
     * @param base, extsdcard DocumentFile
     * @param path, absolute path
     * @param mode
     * @return
     */
    public static int getFdSaf(final Context context, final DocumentFile base, final String path, final String mode) {
        if(context==null) {
            Log.e(LOGTAG, "SafFile.getFdSaf context is null!");
            return 0;
        }
        if(base==null){
            Log.e(LOGTAG, "SafFile.getFdSaf base is null!");
            return 0;
        }
        
        ParcelFileDescriptor pfd = null;
        boolean append = false;
        DocumentFile df2 = null;

        if (mode.indexOf('+') != -1 || mode.indexOf('a') != -1)
            append = true;
        if (mode.indexOf('w') == -1)
            append = true;
        df2 = createFileSaf(base, path, append);
        if (df2 == null) {

            Log.e(LOGTAG, "SafFile.getFdSaf, " + path + " error!");
            return 0;
        }
        try {
            pfd = context.getContentResolver().openFileDescriptor(df2.getUri(), mode);
        } catch (final Exception e) {
            Log.e(LOGTAG, "SafFile.getFdSaf " + e.getClass().getName());
        }
        if (pfd == null)
            return 0;
        return pfd.detachFd();
    }

    /**
     * 
     * @param context
     * @param base, extsdcard DocumentFile
     * @param path, absolute path
     * @param append
     * @return
     */
    public static OutputStream getOutputStreamSaf(final Context context, final DocumentFile base, final String path,
            final boolean append) {
        if(context==null) {
            Log.e(LOGTAG, "SafFile.getOutputStreamSaf context is null!");
            return null;
        }
        if(base==null){
            Log.e(LOGTAG, "SafFile.getOutputStreamSaf base is null!");
            return null;
        }
        
        OutputStream out = null;
        final String mode = append ? "wa" : "w";
        // Log.i(LOGTAG, "getOut.. "+ path +" "+mode);
        final DocumentFile df2 = createFileSaf(base, path, append);
        if (df2 == null) {
            return null;
        }
        try {
            out = context.getContentResolver().openOutputStream(df2.getUri(), mode);
        } catch (final Exception e) {
            Log.e(LOGTAG, "SafFile.getOutputStreamSaf " + e.getClass().getName());
        }
        return out;
    }

    /**
     * 
     * @param context
     * @param base, extsdcard DocumentFile
     * @param path, absolute path
     * @param fileContent
     * @param append
     * @return
     */
    public static boolean writeFileSaf(final Context context, final DocumentFile base, final String path,
            final String fileContent, final boolean append) {
        if(context==null) {
            Log.e(LOGTAG, "SafFile.writeFileSaf context is null!");
            return false;
        }
        if(base==null){
            Log.e(LOGTAG, "SafFile.writeFileSaf base is null!");
            return false;
        }

        final OutputStream out = getOutputStreamSaf(context, base, path, append);
        if (out == null) {
            if(path!=null) 
                Log.e(LOGTAG, "SafFile.writeFileSaf" + path + " error!");
            else 
                Log.e(LOGTAG, "SafFile.writeFileSaf" + " error!");
            return false;
        }
        try {
            out.write(fileContent.getBytes());
            out.flush();
            out.close();
        } catch (final Exception e) {
            Log.e(LOGTAG, "SafFile.writeFileSaf " + e.getClass().getName());
            return false;
        }
        return true;
     }
    
     /**
      * fix the g_context, and g_share
      */
    public static void fixGValues() {
        if (g_context == null) {
            try {
                final Context context = getApplicationContext();
                if (context != null) {
                    final SharedPreferences share = context.getSharedPreferences(g_spname, Context.MODE_PRIVATE);
                    initValues(context, share);
                }
                else{
                    Log.e(LOGTAG, "SafFile.fixGValues() failed, context is null!");
                }
            } catch (final Exception e) {
                Log.e(LOGTAG, "SafFile.checkValues error! " + e.getClass().getName());
            }
        }
        if (g_share == null) {
            final SharedPreferences share = g_context.getSharedPreferences(g_spname, Context.MODE_PRIVATE);
            initValues(g_context, share);
        }
    }
}