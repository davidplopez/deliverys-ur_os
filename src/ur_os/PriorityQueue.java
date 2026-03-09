/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

import java.util.ArrayList;
import java.util.Arrays;

public class PriorityQueue extends Scheduler {

    int currentScheduler;
    private ArrayList<Scheduler> schedulers;

    PriorityQueue(OS os) {
        super(os);
        currentScheduler = -1;
        schedulers = new ArrayList<>();
    }

    PriorityQueue(OS os, Scheduler... s) {
        this(os);
        schedulers.addAll(Arrays.asList(s));
        if (s.length > 0)
            currentScheduler = 0;
    }

    @Override
    public void addProcess(Process p) {

        int priority = p.getPriority();

        if (priority < 0)
            priority = 0;

        if (priority >= schedulers.size())
            priority = schedulers.size() - 1;

        p.setCurrentScheduler(priority);

        schedulers.get(priority).addProcess(p);
    }



 void defineCurrentScheduler() {

    for (int i = 0; i < schedulers.size(); i++) {
        if (!schedulers.get(i).isEmpty()) {
            currentScheduler = i;
            return;
        }
    }

    currentScheduler = -1;
}


 
@Override
public void getNext(boolean cpuEmpty) {

if (currentScheduler == -1 || schedulers.get(currentScheduler).isEmpty()) {
        currentScheduler = -1;
        defineCurrentScheduler();
    }

    if (currentScheduler == -1)
        return;

    Scheduler scheduler = schedulers.get(currentScheduler);

    if (cpuEmpty) {
        scheduler.getNext(true);
        return;
    }

    Process current = os.getProcessInCPU();

    if (current == null)
        return;

    int schedulerIndex = current.getCurrentScheduler();

    if (schedulerIndex < schedulers.size()) {

        currentScheduler = schedulerIndex;

        schedulers.get(currentScheduler).getNext(false);
    }
}
    @Override
    public void newProcess(boolean cpuEmpty) {

    }

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {

        newProcess(cpuEmpty);

    }
}