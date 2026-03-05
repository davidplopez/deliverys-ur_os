package ur_os;

public class SJF_P extends Scheduler {

    public SJF_P(OS os) {
        super(os);
    }

    private Process getShortestProcessInReadyQueue() {
        if (processes.isEmpty()) {
            return null;
        }

        Process shortest = processes.getFirst();
        for (Process p : processes) {
            if (p.getRemainingTimeInCurrentBurst() < shortest.getRemainingTimeInCurrentBurst()) {
                shortest = p;
            } else if (p.getRemainingTimeInCurrentBurst() == shortest.getRemainingTimeInCurrentBurst()) {
                shortest = tieBreaker(shortest, p);
            }
        }

        return shortest;
    }

    @Override
    public void addProcess(Process p) {
        if (p.getState() == ProcessState.CPU) {
            p.setState(ProcessState.READY);
            processes.add(p);
            return;
        }

        if (os.isCPUEmpty()) {
            p.setState(ProcessState.READY);
            processes.add(p);
            getNext(true);
            return;
        }

        Process running = os.getProcessInCPU();
        if (running != null) {
            int pRemaining = p.getRemainingTimeInCurrentBurst();
            int runningRemaining = running.getRemainingTimeInCurrentBurst();

            if (pRemaining < runningRemaining
                    || (pRemaining == runningRemaining && tieBreaker(running, p) == p)) {
                os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, p);
                return;
            }
        }

        p.setState(ProcessState.READY);
        processes.add(p);
    }

    @Override
    public void getNext(boolean cpuEmpty) {
        if (!cpuEmpty || processes.isEmpty()) {
            return;
        }

        Process shortest = getShortestProcessInReadyQueue();

        if (shortest != null) {
            removeProcess(shortest);
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, shortest);
        }
    }

    @Override
    public void newProcess(boolean cpuEmpty) {
    }

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {
    }

    @Override
    public void update() {
        if (os.isCPUEmpty()) {
            getNext(true);
        }
    }
}
