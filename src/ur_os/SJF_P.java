package ur_os;

import java.util.Comparator;

public class SJF_P extends Scheduler {

    public SJF_P(OS os) {
        super(os);
    }

    //  Seleccionar el siguiente proceso y pasarlo a la CPU
    @Override
    public void getNext(boolean cpuEmpty) {
        CPU cpu = os.cpu; // accedemos al CPU del sistema

        if (cpuEmpty && !processes.isEmpty()) {
            // Buscar el proceso con MENOR burst
            Process next = processes.stream()
                    .min(Comparator.comparingInt(Process::getBurstTime))
                    .orElse(null);

            if (next != null) {
                processes.remove(next);    // lo saco de la cola de listos
                cpu.addProcess(next);      // lo meto en la CPU
                addContextSwitch();
            }
        }
    }

    //  Cuando entra un proceso nuevo
    @Override
    public void newProcess(boolean cpuEmpty) {
        CPU cpu = os.cpu;

        if (!cpu.isEmpty()) {
            Process running = cpu.getProcess(); // el que está corriendo ahora

            // Buscar el más corto en la cola de listos
            Process shortest = processes.stream()
                    .min(Comparator.comparingInt(Process::getBurstTime))
                    .orElse(null);

            if (shortest != null && shortest.getBurstTime() < running.getBurstTime()) {
                // Preemption sacar el que está corriendo
                cpu.extractProcess();
                processes.add(running);

                // Ejecutar el más corto
                processes.remove(shortest);
                cpu.addProcess(shortest);
                addContextSwitch();
            }
        } else {
            getNext(true);
        }
    }

    // Cuando un proceso vuelve de I/O
    @Override
    public void IOReturningProcess(boolean cpuEmpty) {
        newProcess(cpuEmpty); // misma lógica que newProcess
    }

    @Override
    public String toString() {
        return "SJF_P ReadyQueue: " + processes.toString();
    }
}
