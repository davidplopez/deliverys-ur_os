package ur_os;
import java.util.Comparator;

public class SJF_P extends Scheduler {

    public SJF_P(OS os) {
        super(os);
    }

    @Override
    public void addProcess(Process p) {
        p.setState(ProcessState.READY);
        processes.add(p);

        if (os.isCPUEmpty()) {
            getNext(true);
            return;
        }

        Process running = os.getProcessInCPU();
        if (running == null) {
            return;
        }

        if (p.getRemainingTimeInCurrentBurst() < running.getRemainingTimeInCurrentBurst()) {
            os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, running);
            removeProcess(p);
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, p);
        }
    }

    @Override
    public void getNext(boolean cpuEmpty) {

        if (!cpuEmpty || processes.isEmpty())
            return;

        Process shortest = processes.stream()
                .min(Comparator.comparingInt(Process::getRemainingTimeInCurrentBurst))
                .orElse(null);

        if (shortest != null) {
            removeProcess(shortest);
            //addContextSwitch();
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, shortest);
        }
    }

    @Override
    public void newProcess(boolean cpuEmpty) {
        if (cpuEmpty) {
            getNext(true);
        }
    }

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {
        newProcess(cpuEmpty);
    }
    @Override
public void update() {

    boolean cpuEmpty = os.isCPUEmpty();

    if (cpuEmpty) {
        getNext(true);
    }
}
}
