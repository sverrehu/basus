package no.shhsoft.basus.ui.ide.debug.variables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import no.shhsoft.basus.language.eval.EvaluationContext;
import no.shhsoft.basus.language.eval.UserFunction;
import no.shhsoft.basus.value.Value;
import no.shhsoft.i18n.I18N;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class VariablesModel
implements TableModel {

    private Variable[] variables = new Variable[0]; /* should never be null */
    private final List<TableModelListener> tableModelListeners = new ArrayList<TableModelListener>();

    private static class Variable
    implements Comparable<Variable> {

        private final String name;
        private final String sortableName;
        private final Value value;

        public Variable(final String name, final Value value) {
            this.name = name;
            this.sortableName = name.toLowerCase();
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Value getValue() {
            return value;
        }

        @Override
        public int compareTo(final Variable other) {
            return sortableName.compareTo(other.sortableName);
        }

    }

    private String toString(final Value value) {
        return value.toString();
    }

    private void notifyTableChanged() {
        final TableModelEvent event = new TableModelEvent(this);
        synchronized (tableModelListeners) {
            for (final TableModelListener tableModelListener : tableModelListeners) {
                tableModelListener.tableChanged(event);
            }
        }
    }

    public VariablesModel() {
        update(null);
    }

    @Override
    public void addTableModelListener(final TableModelListener l) {
        synchronized (tableModelListeners) {
            tableModelListeners.add(l);
        }
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        return String.class;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public synchronized String getColumnName(final int columnIndex) {
        if (columnIndex < 0 && columnIndex > 1) {
            return "???";
        }
        if (columnIndex == 0) {
            return I18N.msg("variables.heading.name");
        }
        return I18N.msg("variables.heading.value");
    }

    @Override
    public synchronized int getRowCount() {
        return variables.length;
    }

    @Override
    public synchronized Object getValueAt(final int rowIndex, final int columnIndex) {
        if (rowIndex < 0 || rowIndex >= variables.length || columnIndex < 0 || columnIndex > 1) {
            return "???";
        }
        if (columnIndex == 0) {
            return variables[rowIndex].getName();
        }
        return toString(variables[rowIndex].getValue());
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return false;
    }

    @Override
    public void removeTableModelListener(final TableModelListener l) {
    }

    @Override
    public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
    }

    public synchronized void update(final EvaluationContext context) {
        if (context == null) {
            if (variables.length != 0) {
                variables = new Variable[0];
            }
            return;
        }
        final Set<String> unsortedVariableNames = context.getVariableNames();
        unsortedVariableNames.remove(UserFunction.RETURN_VARIABLE_NAME);
        String[] variableNames = new String[unsortedVariableNames.size()];
        variableNames = unsortedVariableNames.toArray(variableNames);
        Arrays.sort(variableNames);
        variables = new Variable[variableNames.length];
        for (int q = variableNames.length - 1; q >= 0; q--) {
            final String variableName = variableNames[q];
            variables[q] = new Variable(variableName, context.getVariable(variableName, null));
        }
        notifyTableChanged();
    }

}
