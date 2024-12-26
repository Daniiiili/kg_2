package org.example.kgtask2_9;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Task extends Application {

    private final List<Double> xPoints = new ArrayList<>();
    private final List<Double> yPoints = new ArrayList<>();
    private final double pointRadius = 5; // Радиус точек, которые будем рисовать

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(1000, 800);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Пример точек
        // из википедии для сравнения правильности
        xPoints.add(50.0);
        xPoints.add(100.0);
        xPoints.add(130.0);
        xPoints.add(210.0);

        yPoints.add(100.0);
        yPoints.add(70.0);
        yPoints.add(30.0);
        yPoints.add(160.0);

        redraw(gc);

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            double x = event.getX();
            double y = event.getY();

            xPoints.add(x);
            yPoints.add(y);

            redraw(gc);
        });

        Pane root = new Pane(canvas);
        Scene scene = new Scene(root, 1000, 800);

        stage.setTitle("Интерполяция Лагранжа");
        stage.setScene(scene);
        stage.show();
    }

    // Метод для перерисовки холста
    private void redraw(GraphicsContext gc) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        gc.setFill(Color.RED);
        for (int i = 0; i < xPoints.size(); i++) {
            gc.fillOval(xPoints.get(i) - pointRadius, yPoints.get(i) - pointRadius, pointRadius * 2, pointRadius * 2);
        }

        if (xPoints.size() > 1) {
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(1);

            List<Double> cleanedX = new ArrayList<>();
            List<Double> cleanedY = new ArrayList<>();
            cleanPoints(cleanedX, cleanedY);

            double prevX = cleanedX.get(0);
            double prevY = lagrangeInterpolate(cleanedX, cleanedY, prevX);

            // Проходим по всем x-координатам для рисования линии
            for (double x = cleanedX.get(0); x <= cleanedX.get(cleanedX.size() - 1); x += 0.005) {
                double y = lagrangeInterpolate(cleanedX, cleanedY, x);

                gc.strokeLine(prevX, prevY, x, y);
                prevX = x;
                prevY = y;
            }
        }
    }

    private void cleanPoints(List<Double> cleanedX, List<Double> cleanedY) {
        Map<Double, List<Double>> groupedPoints = new HashMap<>();

        // Перебираем все исходные точки
        for (int i = 0; i < xPoints.size(); i++) {
            double x = xPoints.get(i);
            double y = yPoints.get(i);

            groupedPoints.computeIfAbsent(x, k -> new ArrayList<>()).add(y);
        }

        // Для каждой группы (по x) вычисляем среднее значение y
        for (Map.Entry<Double, List<Double>> entry : groupedPoints.entrySet()) {
            // Добавляем x в очищенный список
            cleanedX.add(entry.getKey());

            // Вычисляем среднее значение y для данного x и добавляем в очищенный список
            cleanedY.add(entry.getValue().stream()
                    .mapToDouble(val -> val)
                    .average()
                    .orElse(0));
        }

            // Сортируем очищенные координаты по возрастанию x
            for (int i = 0; i < cleanedX.size(); i++) {
                // Проходим по всем элементам и сравниваем пары элементов
                for (int j = i + 1; j < cleanedX.size(); j++) {
                    // Если элементы стоят не в порядке возрастания, меняем их местами
                    if (cleanedX.get(i) > cleanedX.get(j)) {
                        // Временные переменные для обмена
                        double tempX = cleanedX.get(i);
                        double tempY = cleanedY.get(i);

                        // Меняем местами элементы в списках
                        cleanedX.set(i, cleanedX.get(j));
                        cleanedY.set(i, cleanedY.get(j));
                        cleanedX.set(j, tempX);
                        cleanedY.set(j, tempY);
                    }
                }
            }
        }


        // Метод для интерполяции Лагранжа
        private double lagrangeInterpolate(List<Double> xPoints, List<Double> yPoints, double x) {
            int n = xPoints.size();
            double result = 0;
            for (int i = 0; i < n; i++) {
                double term = yPoints.get(i);
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        // Рассчитываем каждый множитель для интерполяции
                        term *= (x - xPoints.get(j)) / (xPoints.get(i) - xPoints.get(j));
                    }
                }
                result += term;
            }

            return result;
        }

        public static void main(String[] args) {
            launch(args);
        }
    }