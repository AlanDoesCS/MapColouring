import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.*;

/*
    Algorithm to find how to colour a map using 3 colours:

    1. Assign a random colour to the state with the most borders
    2. Pick a one of the countries that neighbor it with the least amount of neighbors
        a. Assign it one of the 2 remaining colours
        b. Pick a country that borders it with 2 coloured neighbors and is uncoloured
        loop back to a until there are no bordering countries that are uncoloured
    3. loop through all countries and check if they are coloured
            if not coloured, pick one of the remaining colours left by its neighbors
                if no neighbors, assign first colour option in list/array of colours
*/

public class Australia_Colouring {
    public static class cNode {
        public Color defaultColour = Color.DARK_GRAY;
        public Color colour;
        public String id;
        public final int x, y, size_x, size_y;
        public cNode[] Bordering;
        public cNode[] UncolouredBordering;

        public cNode(String n_id, int x_coordinate, int y_coordinate) {
            id = n_id;
            x = x_coordinate;
            y = y_coordinate;
            size_x = 60;
            size_y = 60;
            colour =  defaultColour;
        }
        public void setBordering(cNode[] arr) {
            Bordering = arr;
            UncolouredBordering = arr;
        }
        public void BorderColoured(cNode coloured_state) {
            cNode[] tmp = new cNode[UncolouredBordering.length-1];
            int index = 0;
            for (Australia_Colouring.cNode cNode : UncolouredBordering) {
                if (cNode != coloured_state) {
                    tmp[index] = cNode;
                    index++;
                }
            }
            UncolouredBordering = tmp;
        }

        public void setColour(Color newColour) {
            colour = newColour;
            for (cNode state : UncolouredBordering) {  // Only needed for uncoloured neighbors
                state.BorderColoured(this);
            }
        }
    }
    public static Color find_colour(cNode state, Color[] colors) {
        Color[] available = colors;

        for (cNode Neighbor : state.Bordering) {
            if (Neighbor.colour != Neighbor.defaultColour) { // update list of available colours if one is not available
                Color[] temp = new Color[available.length];

                int index = 0;
                for (int i=0; i< temp.length; i++) {
                    if (available[i] != Neighbor.colour) {
                        temp[index] = available[i];
                        index++;
                    }
                }
                available = temp;
            }
        }
        if (available.length != 0) {
            return available[0];
        } else {
            return state.defaultColour;
        }
    }

    public static void main(String[] args) {
        final Color[] map_colours = {Color.RED, Color.GREEN, Color.BLUE};
        /*
        If you want to add more nodes, it's relatively easy, you have to:
        1.) Create a new Node in the "Create Nodes" section
        2.) Update the borders
            a.) Using the .setBordering() method enter an array of all of its borders
            like this:
            '''
            [new node].setBordering(new cNode[]{ [bordering states / nodes] });
            '''

            b.) Add the node to the array of borders of bordering nations
            e.g. If I wanted to add a new Node "X", I would have to put it into the array of borders of the states that
            border X, for example if it bordered WA, I would have to add it to the array of borders of WA as follows:
            '''
            WA.setBordering(new cNode[]{NT, SA, X}); // Note how X is now in the array
            '''

        3.) Add it to the array of all the states / nodes ("cNode[] states" array)
        */

        // Create Nodes
        cNode WA = new cNode("WA", 10, 75);
        cNode NT = new cNode("NT", 110, 25);
        cNode Q = new cNode("Q", 210, 25);
        cNode NSW = new cNode("NSW", 260, 110);
        cNode V = new cNode("V", 190, 175);
        cNode SA = new cNode("SA", 110, 110);
        cNode T = new cNode("T", 225, 250);

        // Borders
        WA.setBordering(new cNode[]{NT, SA});
        NT.setBordering(new cNode[]{WA, SA, Q});
        Q.setBordering(new cNode[]{NT, SA, NSW});
        NSW.setBordering(new cNode[]{Q, SA, V});
        V.setBordering(new cNode[]{SA, NSW});
        SA.setBordering(new cNode[]{WA, NT, Q, NSW, V});
        T.setBordering(new cNode[]{});

        // Array of all states
        cNode[] states = {WA, NT, Q, T, NSW, V, SA};

        JFrame fr = new JFrame();
        fr.setBounds(10, 10, 500, 500);
        fr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Colouring algorithm
        boolean coloured = false;
        cNode start_state = states[0];
        for (cNode state : states) { // find state with most borders
            if (state.Bordering.length > start_state.Bordering.length) {
                start_state = state;
            }
        }
        start_state.setColour(map_colours[0]);
        cNode current_state = start_state;

        while(!coloured) {
            if (current_state.UncolouredBordering.length != 0) {
                cNode next_state = current_state.UncolouredBordering[0];
                for (cNode bordering : current_state.UncolouredBordering) {
                    if (bordering.UncolouredBordering.length < current_state.UncolouredBordering.length) {
                        next_state = bordering; // update next state if bordering state has fewer uncoloured borders
                    }
                }
                current_state = next_state;
                current_state.setColour(find_colour(current_state, map_colours));
            } else {
                for (cNode state : states) {
                    if (state.colour == state.defaultColour) {
                        state.setColour(find_colour(state, map_colours)); // Attempt to assign a new colour to the state
                    }
                }
                coloured = true;  // end the while loop
            }
        }


        JPanel pn = new JPanel() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (cNode state : states) {
                    g2.setColor(state.colour);
                    g2.fillOval(state.x, state.y, state.size_x, state.size_y);
                    for (cNode Border : state.Bordering) {

                        int p1_x = state.x+state.size_x/2;
                        int p1_y = state.y+state.size_y/2;

                        int p2_x = p1_x + ((Border.x+Border.size_x/2)-p1_x)/2;
                        int p2_y = p1_y + ((Border.y+Border.size_y/2)-p1_y)/2;
                        Line2D line = new Line2D.Float(p1_x, p1_y, p2_x, p2_y);
                        g2.draw(line);
                    }
                }
            }
        };

        //Title
        JLabel title = new JLabel("3-coloured map of Australia - no is colour next to itself");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(10, 325, 500, 25);
        title.setForeground(Color.darkGray);
        fr.add(title);

        // Add State Labels (state ids)
        for (cNode state : states) {
            int padAdj = state.size_x/8 * state.id.length();// Padding adjustment depending on id length
            JLabel newLabel = new JLabel(state.id);
            newLabel.setFont(new Font("Arial", Font.BOLD, 20));
            newLabel.setBounds(state.x+state.size_x/2-padAdj, state.y, state.size_x, state.size_y);
            newLabel.setForeground(Color.WHITE);
            fr.add(newLabel);
        }

        fr.add(pn);
        fr.setVisible(true);
        System.out.println("Displayed Successfully.");
    }
}