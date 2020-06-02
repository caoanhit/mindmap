package com.uit.mindmap.mapdrawer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.uit.mindmap.R;
import com.uit.mindmap.maploader.MapLoader;
import com.uit.mindmap.maploader.NodeData;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MapView extends RelativeLayout {
    private static final int maxNodeAmount = 255;
    private static final int undoAmount = 20;

    private class State {
        List<Integer> ids;
        NodeData[] data;

        public State(List<Integer> ids, NodeData[] data) {
            this.ids = ids;
            this.data = data;
        }

        public State(State state) {
            ids = new ArrayList<>(state.ids);
            data = new NodeData[ids.size()];
            for (int i = 0; i < state.ids.size(); i++) {
                if (state.data[i] == null) {
                    data[i] = new NodeData(nodes[ids.get(i)].data);
                } else {
                    if (nodes[state.ids.get(i)] == null) {

                    } else data[i] = new NodeData(nodes[state.ids.get(i)].data);
                }
            }
        }
    }

    //region Listeners
    public interface onChangeListener {
        public void onChange(boolean undoAvailable, boolean redoAvailable);
    }

    private onChangeListener changeListener;

    public void setOnChangeListener(onChangeListener changeListener) {
        this.changeListener = changeListener;
    }
    //endregion

    Node[] nodes;
    ArrayList<Integer> selectedNodes;
    Paint paint;
    Path path;
    String mapName;
    NodeCustomizer nodeCustomizer;
    boolean changed = false;
    Deque<State> undoHistory;
    Deque<State> redoHistory;

    //region Constructor
    public MapView(Context context) {
        super(context);

        init(null);
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    public void init(@Nullable AttributeSet set) {
        selectedNodes = new ArrayList<>();
        nodes = new Node[maxNodeAmount];
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        path = new Path();
        undoHistory = new ArrayDeque<>();
        redoHistory = new ArrayDeque<>();
    }

    public void setMap(@Nullable Node[] nodes) {
        if (nodes == null) {
            addNode(null);
        } else {
            this.nodes = nodes;
            for (Node node : nodes) {
                if (node != null) {
                    addView(node);
                    node.setMap(this);
                    node.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Node n = (Node) v;
                            selectNode(n.data.id);
                        }
                    });
                    node.applyData();
                }
            }
        }
        selectNode(0);
    }
    //endregion

    //region Undo/Redo
    public State getState() {
        NodeData[] data = new NodeData[selectedNodes.size()];
        List<Integer> ids = new ArrayList<>(selectedNodes);
        for (int i = 0; i < selectedNodes.size(); i++)
            if (!nodes[selectedNodes.get(i)].deleted)
                data[i] = new NodeData(nodes[selectedNodes.get(i)].data);
        return new State(ids, data);
    }

    public void setState(State state) {
        for (int i = 0; i < state.ids.size(); i++) {
            if (state.data[i] == null) {
                deselectAll();
                removeNode(state.ids.get(i));
                Log.i("undo", "Remove node " + state.ids.get(i));
            } else {
                if (nodes[state.ids.get(i)].deleted) {
                    Log.i("undo", "Load node " + state.ids.get(i));
                    loadNode(state.data[i]);
                } else {
                    nodes[state.ids.get(i)].setData(new NodeData(state.data[i]));
                    nodes[state.ids.get(i)].applyData();
                    setSheetData();
                }
            }
        }
    }

    public void releaseState(State state) {
        for (int i = 0; i < state.ids.size(); i++) {
            if (state.data[i] == null) {
                nodes[nodes[state.ids.get(i)].data.parent].removeChildren(nodes[state.ids.get(i)].data.id);
                nodes[state.ids.get(i)] = null;
            }
        }
    }

    public boolean undo() {
        if (!undoHistory.isEmpty()) {
            State state = undoHistory.pollLast();


            redoHistory.add(state);
            redoHistory.add(new State(state));
            setChanged();
            setState(state);
            Log.i("undo", "Remain " + undoHistory.size());
            return true;
        } else return false;
    }

    public boolean redo() {
        if (!redoHistory.isEmpty()) {
            State state = redoHistory.pollLast();


            undoHistory.add(redoHistory.pollLast());
            setChanged();
            setState(state);
            return true;
        } else return false;
    }

    public void addCommand() {
        undoHistory.add(getState());
        Log.i("command", "" + undoHistory.size());
        redoHistory.clear();
        setChanged();
        if (undoHistory.size() > undoAmount) {
            State state= undoHistory.pollFirst();
            releaseState(state);
        }

    }

    public void addCommandAddNode() {
        undoHistory.add(new State(new ArrayList<Integer>(selectedNodes), new NodeData[selectedNodes.size()]));
        Log.i("command addnode", selectedNodes.size() + "");
        clearRedo();
        setChanged();
        if (undoHistory.size() > undoAmount) undoHistory.pollFirst();
    }

    private void clearRedo(){
        for (int i=0; i< redoHistory.size()/2; i++){
            State state= redoHistory.pollLast();
            releaseState(state);
            redoHistory.pollLast();
        }
    }
    public void setChanged() {

        changed = true;
        if (changeListener != null) {
            Log.i("onChange", "setChanged");
            changeListener.onChange(!undoHistory.isEmpty(), !redoHistory.isEmpty());
        }
    }
    //endregion

    //region Draw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < maxNodeAmount; i++) {
            if (nodes[i] != null && nodes[i].data != null) {
                for (int a : nodes[i].data.children) {
                    if (!nodes[a].deleted)
                    drawLine(i, a, canvas);
                }
            }
        }
    }

    private void drawLine(int parent_node, int child_node, Canvas canvas) {
        paint.setColor(nodes[child_node].data.lineColor);
        paint.setStrokeWidth(getContext().getResources().getDimensionPixelSize(R.dimen.thin_line));
        float[] intervals;
        int style = nodes[child_node].data.lineStyle;
        switch (style) {
            case 0:
                paint.setPathEffect(null);
                break;
            case 1:
                intervals = new float[]{15, 15};
                paint.setPathEffect(new DashPathEffect(intervals, 0));
                break;
            case 2:
                intervals = new float[]{15, 15, 5, 15};
                paint.setPathEffect(new DashPathEffect(intervals, 0));
                break;
        }
        drawCurve(parent_node, child_node, canvas);
    }

    private void drawCurve(int parent_node, int child_node, Canvas canvas) {
        path.reset();
        int[] p = nodes[parent_node].anchor(nodes[child_node].data.pos);
        int[] c = nodes[child_node].anchor(nodes[parent_node].data.pos);
        int[] s = new int[2];
        getLocationOnScreen(s);
        float scale = ((MapDrawerActivity) getContext()).zoomLayout.scale;
        p[0] /= scale;
        p[1] /= scale;
        c[0] /= scale;
        c[1] /= scale;
        p[0] -= (int) (s[0] / scale);
        p[1] -= (int) (s[1] / scale);
        c[0] -= (int) (s[0] / scale);
        c[1] -= (int) (s[1] / scale);
        path.moveTo(p[0], p[1]);
        path.cubicTo(p[0] * c[3] + (c[0] / 2 + p[0] / 2) * c[2]
                , p[1] * p[2] + p[3] * (c[1] / 2 + p[1] / 2)
                , c[0] * p[3] + (p[0] / 2 + c[0] / 2) * p[2]
                , c[1] * c[2] + c[3] * (p[1] / 2 + c[1] / 2), c[0], c[1]);
        canvas.drawPath(path, paint);
    }

    private void drawStraightLine(int parent_node, int child_node, Canvas canvas) {
        paint.setAntiAlias(true);
        int[] p = nodes[parent_node].anchor(nodes[child_node].data.pos);
        int[] c = nodes[child_node].anchor(nodes[parent_node].data.pos);
        int[] s = new int[2];
        getLocationOnScreen(s);
        float scale = ((MapDrawerActivity) getContext()).zoomLayout.scale;
        p[0] /= scale;
        p[1] /= scale;
        c[0] /= scale;
        c[1] /= scale;
        p[0] -= (int) (s[0] / scale);
        p[1] -= (int) (s[1] / scale);
        c[0] -= (int) (s[0] / scale);
        c[1] -= (int) (s[1] / scale);
        canvas.drawLine(p[0], p[1], c[0], c[1], paint);
    }

    private void drawTriangle(int parent_node, int child_node, int side, Canvas canvas) {

    }
    //endregion

    //region Add
    public int addNode(@Nullable int[] pos) {
        Node node = new Node(getContext());
        addView(node);
        int i = 0;
        while (nodes[i] != null && !nodes[i].deleted && i < 254) {
            i++;
        }
        nodes[i] = node;
        node.setFillColor(getContext().getResources().getColor(R.color.colorPrimary));
        node.setTextColor(Color.WHITE);
        node.setOutlineColor(getContext().getResources().getColor(R.color.colorPrimary));
        node.data.id = i;
        node.setMap(this);
        selectNode(i);
        if (pos != null) {
            node.setPosition(pos);
        } else {
            pos = new int[2];
            pos[0] += getContext().getResources().getDimension(R.dimen.map_size) / 2;
            pos[1] += getContext().getResources().getDimension(R.dimen.map_size) / 2;
            node.setPosition(pos);
        }
        node.applyData();
        node.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Node n = (Node) v;
                selectNode(n.data.id);
            }
        });
        return i;
    }

    public int addNode(int parent) {
        Node node = new Node(getContext());
        addView(node);
        node.data.parent = parent;
        int i = 0;
        while (nodes[i] != null && i < 254) {
            i++;
        }
        node.data.id = i;
        nodes[i] = node;
        node.setMap(this);
        node.applyData();
        nodes[parent].addChild(i);
        int[] pos = new int[2];
        pos[0] = nodes[parent].data.pos[0];
        pos[1] = nodes[parent].data.pos[1];
        pos[0] += 50 + nodes[parent].getWidth();
        node.setPosition(pos);
        node.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Node n = (Node) v;
                selectNode(n.data.id);
            }
        });
        return i;
    }

    public void addNode() {
        Log.i("add node", "" + selectedNodes.get(0));
        for (int i : selectedNodes) {
            deselectNode(i);
            int node = addNode(i);
            selectMultiple(node);
            nodes[node].setText("New node");
        }
        addCommandAddNode();
        LayoutInflater li = LayoutInflater.from(getContext());
        View customDialogView = li.inflate(R.layout.edit_text_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(customDialogView);
        final EditText etName = (EditText) customDialogView.findViewById(R.id.name);
        etName.setText(nodes[selectedNodes.get(0)].getText());
        etName.selectAll();
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                for (int i : selectedNodes) {
                    nodes[i].setText(etName.getText().toString());
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Log.i("command", "" + undoHistory.size());
    }

    public void loadNode(NodeData data) {
        nodes[data.id].deleted = false;
        addView(nodes[data.id]);
    }
    //endregion

    //region Remove
    public void removeChildNode(int id) {
        for (int i : nodes[id].data.children) removeChildNode(i);
        removeView(nodes[id]);
        nodes[id].deleted=true;
    }

    public void removeNode(int id) {
        Log.i("remove", "" + id);
        if (id != 0) {
            for (int i : nodes[id].data.children) removeChildNode(i);
            removeView(nodes[id]);
            nodes[id].deleted = true;
        } else Toast.makeText(getContext(), "Cannot delete root node", Toast.LENGTH_SHORT).show();
    }

    public void removeNode() {
        if (selectedNodes.size() != 1 || selectedNodes.get(0) != 0)
            addCommand();
        for (int i : selectedNodes) {
            removeNode(i);
        }
        selectedNodes.clear();
    }

    //endregion

    //region Selection
    public void deselectNode(int id) {
        nodes[id].defocus();
        selectedNodes.remove((Integer) id);
    }

    public void deselectAll() {
        for (int id : selectedNodes) {
            nodes[id].defocus();
        }
        View menu = ((MapDrawerActivity) getContext()).menu;
        if (menu != null) menu.setVisibility(GONE);
        selectedNodes.clear();
    }

    public void selectMultiple(int id) {
        selectedNodes.add(id);
        nodes[id].focus();
        nodes[id].bringToFront();
        View menu = ((MapDrawerActivity) getContext()).menu;
        BottomSheetBehavior bottomSheetBehavior = ((MapDrawerActivity) getContext()).bottomSheetBehavior;
        if (menu != null) menu.setVisibility(VISIBLE);
        if (bottomSheetBehavior != null)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void selectNode(int id) {
        deselectAll();
        selectedNodes.add(id);
        nodes[id].focus();
        nodes[id].bringToFront();
        View menu = ((MapDrawerActivity) getContext()).menu;
        BottomSheetBehavior bottomSheetBehavior = ((MapDrawerActivity) getContext()).bottomSheetBehavior;
        if (menu != null) menu.setVisibility(VISIBLE);
        if (bottomSheetBehavior != null)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void toggleSelect(int id) {
        int index = selectedNodes.indexOf(id);
        if (index > -1) deselectNode(selectedNodes.get(index));
        else selectMultiple(id);
    }
    //endregion

    //region Customization
    public void setTextSize(int text_size) {
        addCommand();
        Log.i("command", "Text size");
        for (int i : selectedNodes) nodes[i].setTextSize(text_size);
    }

    public void setTextColor(int color) {
        addCommand();
        Log.i("command", "Text color");
        for (int i : selectedNodes) nodes[i].setTextColor(color);
    }

    public void setFillColor(int color) {
        addCommand();
        Log.i("command", "Fill color");
        for (int i : selectedNodes) nodes[i].setFillColor(color);
    }

    public void setOutlineColor(int color) {
        addCommand();
        Log.i("command", "Outline color");
        for (int i : selectedNodes) nodes[i].setOutlineColor(color);
    }

    public void setLineStyle(int style) {
        addCommand();
        Log.i("command", "Line style");
        for (int i : selectedNodes) nodes[i].setLineStyle(style);
    }

    public void setLineColor(int color) {
        addCommand();
        Log.i("command", "Line color");
        for (int i : selectedNodes) nodes[i].setLineColor(color);
    }

    public void setNodeCustomizer(NodeCustomizer customizer) {
        nodeCustomizer = customizer;
        customizer.setMapView(this);
    }

    public void setSheetData() {
        if (!selectedNodes.isEmpty())
            nodeCustomizer.setData(nodes[selectedNodes.get(0)].data);
    }
    //endregion

    //region Text
    public void editText() {

        LayoutInflater li = LayoutInflater.from(getContext());
        View customDialogView = li.inflate(R.layout.edit_text_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(customDialogView);
        final EditText etName = (EditText) customDialogView.findViewById(R.id.name);
        etName.setText(nodes[selectedNodes.get(0)].getText());
        etName.selectAll();
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                addCommand();
                for (int i : selectedNodes) {
                    nodes[i].setText(etName.getText().toString());
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void setText(String text) {
        for (int i : selectedNodes) nodes[i].setText(text);
    }

    public void moveNode(int x, int y) {
        for (int i : selectedNodes) {
            nodes[i].movePosition(x, y);
        }
    }
    //endregion

    //region Save/Load
    public NodeData[] getData() {
        NodeData[] data = new NodeData[maxNodeAmount];
        for (int i = 0; i < maxNodeAmount; i++) {
            if (nodes[i] != null && !nodes[i].deleted) {
                data[i] = new NodeData(nodes[i].data);
                for(int a:nodes[i].data.children){
                    if (nodes[a]==null || nodes[a].deleted) data[i].children.remove(data[i].children.indexOf(a));
                }
            }
        }
        return data;
    }

    public void saveData() {
        if (mapName == null) {
            saveAs();
        } else {
            MapLoader loader = new MapLoader(getContext());
            if (loader.saveMap(mapName, getData())) {
                Toast.makeText(getContext(), "Map saved to \"" + mapName + "\"", Toast.LENGTH_SHORT).show();
                loader.saveThumbnail(mapName, getThumbnail());
                changed = false;
            } else
                Toast.makeText(getContext(), "Error: Cannot save map", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveAs() {
        LayoutInflater li = LayoutInflater.from(getContext());
        View customDialogView = li.inflate(R.layout.edit_text_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(customDialogView);
        final EditText etName = (EditText) customDialogView.findViewById(R.id.name);
        ((TextView) customDialogView.findViewById(R.id.tv_dialog)).setText(R.string.map_name);
        etName.setText(nodes[selectedNodes.get(0)].getText());
        etName.selectAll();
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mapName = etName.getText().toString();
                final MapLoader loader = new MapLoader(getContext());
                if (loader.mapExist(mapName)) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setMessage("Map already exist. Do you want to overwrite?");
                    alertDialog.setIcon(R.mipmap.ic_launcher);
                    alertDialog.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (loader.saveMap(mapName, getData())) {
                                loader.saveThumbnail(mapName, getThumbnail());
                                changed = false;
                            }
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                    return;
                }
                if (loader.saveMap(mapName, getData())) {
                    loader.saveThumbnail(mapName, getThumbnail());
                    changed = false;
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public void loadMap(@Nullable String mapName) {
        this.mapName = mapName;
        if (mapName == null) setMap(null);
        else {
            MapLoader loader = new MapLoader(getContext());
            NodeData[] data = loader.loadMap(mapName);
            if (data != null) {
                Node[] nodes = new Node[maxNodeAmount];
                for (int i = 0; i < maxNodeAmount; i++) {
                    if (data[i] != null) {
                        nodes[i] = new Node(getContext());
                        nodes[i].setData(data[i]);
                    }
                }
                setMap(nodes);
            }
        }
    }

    //endregion

    //regionDimensions
    private int[] getMapCenter() {
        int[] a = new int[2];
        int maxX = nodes[0].data.pos[0];
        int maxY = nodes[0].data.pos[1];
        int minX = nodes[0].data.pos[0];
        int minY = nodes[0].data.pos[1];
        for (Node node : nodes) {
            if (node != null && !node.deleted) {
                if (node.data.pos[0] > maxX) maxX = node.data.pos[0];
                if (node.data.pos[1] > maxY) maxY = node.data.pos[1];
                if (node.data.pos[0] < minX) minX = node.data.pos[0];
                if (node.data.pos[1] < minY) minY = node.data.pos[1];
            }
        }
        a[0] = (maxX + minX) / 2;
        a[1] = (maxY + minY) / 2;
        return a;
    }

    private int getMapWidth() {
        int max = nodes[0].data.pos[0];
        int min = nodes[0].data.pos[0];
        for (Node node : nodes) {
            if (node != null && !node.deleted) {
                if (node.data.pos[0] > max) max = node.data.pos[0];
                if (node.data.pos[0] < min) min = node.data.pos[0];
            }
        }
        return (max + min) / 2;
    }

    private int getMapHeight() {
        int max = nodes[0].data.pos[1];
        int min = nodes[0].data.pos[1];
        for (Node node : nodes) {
            if (node != null && !node.deleted) {
                if (node.data.pos[1] > max) max = node.data.pos[1];
                if (node.data.pos[1] < min) min = node.data.pos[1];
            }
        }
        return (max + min) / 2;
    }

    private int[] getMapDimensions() {
        int[] a = new int[4];
        int maxX = nodes[0].data.pos[0];
        int maxY = nodes[0].data.pos[1];
        int minX = nodes[0].data.pos[0];
        int minY = nodes[0].data.pos[1];
        for (Node node : nodes) {
            if (node != null && !node.deleted) {
                if (node.data.pos[0] > maxX) maxX = node.data.pos[0];
                if (node.data.pos[1] > maxY) maxY = node.data.pos[1];
                if (node.data.pos[0] < minX) minX = node.data.pos[0];
                if (node.data.pos[1] < minY) minY = node.data.pos[1];
            }
        }
        a[0] = minX - 100;
        a[1] = minY - 100;
        a[2] = maxX + 100;
        a[3] = maxY + 100;
        return a;
    }

    public Bitmap getThumbnail() {
        int[] d = getMapDimensions();
        int[] a = getMapCenter();
        Bitmap b = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        this.layout(getLeft(), getTop(), getRight(), getBottom());
        Log.i("Left", "" + d[0]);
        Log.i("Top", "" + d[1]);
        Log.i("Right", "" + d[2]);
        Log.i("Bottom", "" + d[3]);
        this.draw(c);
        int width = Math.max((d[3] - d[1]) * 160 / 120, d[2] - d[0]);
        int height = Math.max((d[2] - d[0]) * 120 / 160, d[3] - d[1]);
        return Bitmap.createScaledBitmap(Bitmap.createBitmap(b, a[0] - width / 2, a[1] - height / 2, width, height), 320, 240, true);
    }
    //endregion
}
