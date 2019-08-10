package visualisation.processor.helpers;


import javafx.collections.FXCollections;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import scheduler.ProcessorBlock;
import scheduler.State;
import visualisation.processor.ProcessChart;
import visualisation.AlgorithmDataStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Singleton class for creating a process chart.
 * TODO: Remove magic numbers :)
 */
public class ProcessChartHelper {
    private final NumberAxis xAxis = new NumberAxis();
    private final CategoryAxis yAxis = new CategoryAxis();
    private final ProcessChart<Number,String> chart = new ProcessChart<>(xAxis,yAxis);
    private HashMap<Integer,XYChart.Series> seriesMap = new HashMap();
    private Pane processPane;
    //TODO: Make this apply to whatever the user inputs
    private int numberOfProcessors;
    public ProcessChartHelper(Pane processPane) {
        numberOfProcessors = AlgorithmDataStorage.getInstance().getNumberOfProcessors();
        this.processPane = processPane;
        initialiseXAxis();
        initialiseYAxis();
        initialiseSettings();

        test();
       // seriesMap.keySet().forEach(key-> chart.getData().add(seriesMap.get(key)));
    }

    private void test() {
        State finalState = AlgorithmDataStorage.getInstance().getState();
        XYChart.Series series1 = new XYChart.Series();
        for (int i : seriesMap.keySet()) {
            List<ProcessorBlock> processBlocks = finalState.getProcessors().get(i).getProcessorBlockList();
            for (ProcessorBlock block : processBlocks) {
                series1.getData().add(new XYChart.Data(block.getStartTime(),"Processor "+(i+1),new ChartData(block.getEndTime() - block.getStartTime(),"status-blue")));
            }
        }
        chart.getData().add(series1);
    }

    /**
     * Retrieves the process chart that has been created by the helper
     * @return
     */
    public ProcessChart getProcessChart() {
        return chart;
    }

    /**
     * Sets the labels for the xaxis
     */
    private void initialiseXAxis() {
        xAxis.setLabel("Time");
        xAxis.setMinorTickCount(4);
    }

    /**
     * Sets the labels for the y axis
     * Also sets the height each processor uses
     */
    private void initialiseYAxis() {
        yAxis.setTickLabelGap(20);
        // The y category will just be the number of processors
        List<String> processors = new ArrayList<>();

        for(int processorNo = 0; processorNo < numberOfProcessors; processorNo++){
            processors.add("Processor " + (processorNo+1));
            //Maps the processor to possible series
            seriesMap.put(processorNo,new XYChart.Series());
        }
        chart.setProcessorHeight((processPane.getPrefHeight()-200)/numberOfProcessors);
        yAxis.setCategories(FXCollections.observableArrayList(processors));

    }

    /**
     * Initialises the basic settings for the process chart
     */
    private void initialiseSettings() {
        chart.setTitle("Process Table");
        chart.setLegendVisible(false);
        chart.setPrefHeight(processPane.getPrefHeight());
        chart.setPrefWidth(processPane.getPrefWidth());
        chart.getStylesheets().add("visualisation/visualisationassets/ProcessChart.css");
    }

}
