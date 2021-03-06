package visualisation.processor;

import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import visualisation.processor.helpers.ChartData;

import java.util.*;

/**
 * A class which represents the chart that is being displayed
 * A lot of code has been inspired from: https://stackoverflow.com/questions/27975898/gantt-chart-from-scratch
 * @param <X>
 * @param <Y>
 */
public class ProcessChart<X,Y> extends XYChart<X,Y> {
    private double processorHeight = 10;
    private String name;
    private final int DEFAULT_Y_SCALING = 1;
    private final int DEFAULT_X_SCALING = 1;
    private final double Y_BLOCK_SCALING  = 2.0;
    private boolean beenSeen = false;

    public ProcessChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
        super(xAxis,yAxis);
        setData(FXCollections.observableArrayList());
    }
    /**
     * Determines how to plot the tasks.
     */
    @Override
    protected void layoutPlotChildren() {
        for (int index = 0; index < getData().size(); index++) {
            Series<X,Y> series = getData().get(index);
            Iterator<Data<X,Y>> seriesIterator = getDisplayedDataIterator(series);
            while(seriesIterator.hasNext()) {

                Data<X,Y> item = seriesIterator.next();
                double x = getXAxis().getDisplayPosition(item.getXValue());
                double y = getYAxis().getDisplayPosition(item.getYValue());

                //If either value is not present, skip over it.
                if (Double.isNaN(x) || Double.isNaN(y)) {
                    continue;
                }
                Node block = item.getNode();
                Rectangle taskVisual = new Rectangle( getLength( item.getExtraValue()), getProcessorHeight());
                StackPane region = (StackPane)item.getNode();
                taskVisual.setWidth( getLength( item.getExtraValue()) * ((getXAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getXAxis()).getScale()) : DEFAULT_X_SCALING));
                y -= getProcessorHeight() / Y_BLOCK_SCALING;

                setRegionInfo(region,taskVisual);

                block.setLayoutX(x);
                block.setLayoutY(y);
                createBlockText(taskVisual,getNodeName(item.getExtraValue()),region);
            }
        }
    }

    private void createBlockText(Rectangle rectangle, String textToBeInputted, StackPane pane) {
        // Set the text for the weight of each task to the center of the rectangle
        Text text = new Text(textToBeInputted);
        text.setFill(Color.WHITE);
        Group group = new Group(text);
        group.setTranslateY(rectangle.getHeight()/2);
        group.setTranslateX(rectangle.getWidth()/2);
        pane.getChildren().add(group);
    }

    /**
     *  Basic settings for the task pane
     * @param region
     * @param taskVisual
     */
    private void setRegionInfo(StackPane region,Rectangle taskVisual) {
        region.setShape(null);
        region.setShape(taskVisual);
        region.setScaleShape(false);
        region.setCenterShape(false);
        region.setCacheShape(false);
    }

    /**
     * Retrieve the height for a processor visual
     * @return
     */
    public double getProcessorHeight() {
        return processorHeight;
    }

    /**
     * Sets the processor height visual
     * @param processorHeight
     */
    public void setProcessorHeight(double processorHeight) {
        this.processorHeight = processorHeight;
    }


    @Override
    protected  void seriesAdded(Series<X,Y> series, int seriesIndex) {
        for (int j=0; j<series.getData().size(); j++) {
            Data<X,Y> item = series.getData().get(j);
            Node container = createContainer(item);
            getPlotChildren().add(container);
        }
    }

    /**
     * Creates a container for the data items to sit in
     * @param item
     * @return
     */
    private Node createContainer(Data<X,Y> item) {
        Node container = item.getNode();

        if (container == null) {
            container = new StackPane();
            item.setNode(container);
        }
        container.getStyleClass().add( getStyleClass( item.getExtraValue()));

        return container;
    }

    /**
     * Updates the axis range, which depends on the values given for the task
     */
    @Override
    public void updateAxisRange() {
        Axis<X> xAxis = getXAxis();
        Axis<Y> yAxis = getYAxis();
        List<X> xData= null;
        List<Y> yData = null;
        if(xAxis.isAutoRanging()) {
            xData = new ArrayList<X>();
        }
        if(yAxis.isAutoRanging()) {
            yData = new ArrayList<>();
        }

        if(xData != null || yData != null) {
            for(Series<X,Y> series : getData()) {
                for(Data<X,Y> data: series.getData()) {
                    if(xData != null) {
                        xData.add(data.getXValue());
                        // Increases the xaxis based on the length of each task
                        xData.add(xAxis.toRealValue(xAxis.toNumericValue(data.getXValue()) + getLength(data.getExtraValue())));
                    }
                }
            }
            if(xData != null) {
                xAxis.invalidateRange(xData);
            }
        }
    }

    /**
     * Retrieves the style class from a chart data object
     * @param obj
     * @return
     */
    private String getStyleClass( Object obj) {
        return ((ChartData) obj).getStyleClass();
    }

    /**
     * Retrieves length of a task from a chart data object
     * @param obj
     * @return
     */
    private double getLength( Object obj) {
        return ((ChartData) obj).getLength();
    }

    private String getNodeName(Object obj) {return ((ChartData) obj).getText();}
    @Override
    public  void dataItemRemoved(Data<X,Y> item,Series<X,Y> series) {
        for (Data<X,Y> d : series.getData()) {
            final Node container = d.getNode();
            getPlotChildren().remove(container);
        }
        removeSeriesFromDisplay(series);
    }
    @Override
    protected void dataItemChanged(Data<X, Y> item) {
    }
    @Override
    protected void dataItemAdded(Series<X,Y> series, int itemIndex, Data<X,Y> item) {
    }
    @Override
    protected void seriesRemoved(Series<X,Y> series) {
    }
}
