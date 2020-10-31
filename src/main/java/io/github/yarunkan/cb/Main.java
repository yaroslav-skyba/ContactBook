package io.github.yarunkan.cb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static final String FILE_CONTACTS_PATH = "src/main/resources/Contacts.csv";
    private static final String CONTACT_DATA_SEPARATOR = ",";

    private Main() {

        final JFrame contactBookJF = new JFrame("Contact Book");
        
        contactBookJF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contactBookJF.setExtendedState(JFrame.MAXIMIZED_BOTH);
        contactBookJF.setLayout(new BorderLayout());
        contactBookJF.setVisible(true);

        createMainPage(contactBookJF);
    }

    private void createMainPage(JFrame contactBookJF) {

        final Map<String, String> contacts = readContactsFile();
        final JPanel searchContactJP = new JPanel(new BorderLayout());
        final JPanel contactsJP = new JPanel(new GridLayout(contacts.size(), 1, 0, 10));
        final JScrollPane[] contactsJSP = {new JScrollPane(contactsJP)};
        final JLabel searchContactJL = new JLabel("Search contact");
        final JTextField searchContactJTF = new JTextField();
        final JButton createContactJB = new JButton("Create contact");

        contactBookJF.add(searchContactJP, BorderLayout.NORTH);
        contactBookJF.add(contactsJSP[0], BorderLayout.CENTER);
        contactBookJF.add(createContactJB, BorderLayout.SOUTH);

        searchContactJP.add(searchContactJL, BorderLayout.NORTH);
        searchContactJP.add(searchContactJTF, BorderLayout.SOUTH);

        for (Map.Entry<String, String> contactEntry : contacts.entrySet()) {

            final JButton contactJB = new JButton(contactEntry.getKey());

            contactsJP.add(contactJB);

            contactJB.addActionListener(getContactButtonActionListener(contactJB,
                                                                   contacts,
                                                                   contactBookJF,
                                                                   searchContactJP,
                                                                   contactsJSP[0],
                                                                   createContactJB));
        }

        createContactJB.addActionListener(e -> {

            final JPanel createContactJP = new JPanel(new BorderLayout());
            final JPanel contactNameJP = new JPanel(new BorderLayout());
            final JPanel contactNumberJP = new JPanel(new BorderLayout());
            final JPanel createContactButtonsJP = new JPanel(new BorderLayout());
            final JLabel contactNameJL = new JLabel("Contact name");
            final JLabel contactNumberJL = new JLabel("Contact phone number");
            final JTextField contactNameJTF = new JTextField();
            final JTextField contactNumberJTF = new JTextField();
            final JButton saveContactJB = new JButton("Save contact");
            final JButton returnMainPageJB = new JButton("Return to the main page");

            contactBookJF.getContentPane().remove(searchContactJP);
            contactBookJF.getContentPane().remove(createContactJB);
            contactBookJF.getContentPane().add(createContactJP, BorderLayout.NORTH);
            contactBookJF.getContentPane().add(createContactButtonsJP, BorderLayout.SOUTH);
            contactBookJF.getContentPane().revalidate();
            contactBookJF.getContentPane().repaint();

            createContactJP.add(contactNameJP, BorderLayout.NORTH);
            createContactJP.add(contactNumberJP, BorderLayout.SOUTH);

            contactNameJP.add(contactNameJL, BorderLayout.NORTH);
            contactNameJP.add(contactNameJTF, BorderLayout.SOUTH);

            contactNumberJP.add(contactNumberJL, BorderLayout.NORTH);
            contactNumberJP.add(contactNumberJTF, BorderLayout.SOUTH);

            createContactButtonsJP.add(saveContactJB, BorderLayout.NORTH);
            createContactButtonsJP.add(returnMainPageJB, BorderLayout.SOUTH);

            saveContactJB.addActionListener(e1 -> {

                final String[] contact = {contactNameJTF.getText(), contactNumberJTF.getText()};

                if (!contact[0].isBlank() && !contact[1].isBlank()) {

                    writeContact(contact);

                    contacts.put(contact[0], contact[1]);

                    final JButton savedContactJB = new JButton(contact[0]);

                    contactsJP.add(savedContactJB);
                    contactsJP.setLayout(new GridLayout(contacts.size(), 1, 0, 10));

                    contactBookJF.getContentPane().add(searchContactJP, BorderLayout.NORTH);
                    contactBookJF.getContentPane().add(createContactJB, BorderLayout.SOUTH);
                    contactBookJF.getContentPane().remove(createContactJP);
                    contactBookJF.getContentPane().remove(createContactButtonsJP);
                    contactBookJF.getContentPane().revalidate();
                    contactBookJF.getContentPane().repaint();

                    savedContactJB.addActionListener(getContactButtonActionListener(savedContactJB,
                                                                                contacts,
                                                                                contactBookJF,
                                                                                searchContactJP,
                                                                                contactsJSP[0],
                                                                                createContactJB));
                }
            });

            returnMainPageJB.addActionListener(e12 -> {

                contactBookJF.getContentPane().add(searchContactJP, BorderLayout.NORTH);
                contactBookJF.getContentPane().add(createContactJB, BorderLayout.SOUTH);
                contactBookJF.getContentPane().remove(createContactJP);
                contactBookJF.getContentPane().remove(createContactButtonsJP);
                contactBookJF.getContentPane().revalidate();
                contactBookJF.getContentPane().repaint();
            });
        });

        searchContactJTF.addCaretListener(e -> {

            final Map<String, String> searchedContacts = new HashMap<>();

            for (Map.Entry<String, String> contactEntry : contacts.entrySet()) {

                final String searchedContact = searchContactJTF.getText();
                final String contactName = contactEntry.getKey();
                final String contactNumber = contactEntry.getValue();

                if (contactName.contains(searchedContact) || contactNumber.contains(searchedContact)) {

                    searchedContacts.put(contactName, contactNumber);
                }
            }

            contactsJP.removeAll();
            contactsJP.revalidate();
            contactsJP.repaint();
            contactsJP.setLayout(new GridLayout(searchedContacts.size(), 1, 0, 10));

            for (Map.Entry<String, String> contactEntry : searchedContacts.entrySet()) {

               contactsJP.add(new JButton(contactEntry.getKey()));
            }
            
            contactBookJF.getContentPane().remove(contactsJSP[0]);
            contactBookJF.getContentPane().revalidate();
            contactBookJF.getContentPane().repaint();

            contactsJSP[0] = new JScrollPane(contactsJP);

            contactBookJF.getContentPane().add(contactsJSP[0], BorderLayout.CENTER);

            for (Component searchedComponent : contactsJP.getComponents()) {

                if (searchedComponent instanceof AbstractButton) {

                    final AbstractButton searchedButton = (JButton)searchedComponent;

                    searchedButton.addActionListener(getContactButtonActionListener(searchedButton,
                                                                                contacts,
                                                                                contactBookJF,
                                                                                searchContactJP,
                                                                                contactsJSP[0],
                                                                                createContactJB));
                }
            }
        });
    }

    private Map<String, String> readContactsFile() {

        final Map<String, String> contacts = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_CONTACTS_PATH))) {

            String contact;

            while ((contact = reader.readLine()) != null) {

                final String[] contactTokens = contact.split(CONTACT_DATA_SEPARATOR);

                contacts.put(contactTokens[0], contactTokens[1]);
            }
        } catch (IOException e) {

            System.out.println(e.getMessage());
        }

        return contacts;
    }

    private void writeContact(String[] contact) {

        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_CONTACTS_PATH, true))) {

            writer.write(contact[0] + CONTACT_DATA_SEPARATOR);
            writer.write(contact[1] + "\n");

        } catch (IOException e) {

            System.out.println(e.getMessage());
        }
    }

    private ActionListener getContactButtonActionListener(AbstractButton button,
                                                          Map<String, String> contacts,
                                                          JFrame contactBookJF,
                                                          JPanel searchContactJP,
                                                          JScrollPane contactsJSP,
                                                          JButton createContactJB) {

        class ContactData implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {

                final int contactDataJPRowsCount = 2;
                final int contactDataJPRHeightGap = 10;
                final JPanel contactDataJP = new JPanel(new GridLayout(contactDataJPRowsCount, 2, contactDataJPRHeightGap, 10));
                final String pressedJBText = button.getText();
                final JLabel contactNameTestJL = new JLabel("The contact name");
                final JLabel contactNameJL = new JLabel(pressedJBText);
                final JLabel contactNumberTextJL = new JLabel("The contact phone number");
                final JLabel contactNumberJL = new JLabel(contacts.get(pressedJBText));
                final JButton returnMainPageJB = new JButton("Return to the main page");

                contactDataJP.add(contactNameTestJL);
                contactDataJP.add(contactNameJL);
                contactDataJP.add(contactNumberTextJL);
                contactDataJP.add(contactNumberJL);

                contactBookJF.getContentPane().add(contactDataJP, BorderLayout.NORTH);
                contactBookJF.getContentPane().add(returnMainPageJB, BorderLayout.SOUTH);
                contactBookJF.getContentPane().remove(searchContactJP);
                contactBookJF.getContentPane().remove(contactsJSP);
                contactBookJF.getContentPane().remove(createContactJB);
                contactBookJF.getContentPane().revalidate();
                contactBookJF.getContentPane().repaint();

                returnMainPageJB.addActionListener(e13 -> {

                    contactBookJF.getContentPane().add(searchContactJP, BorderLayout.NORTH);
                    contactBookJF.getContentPane().add(contactsJSP, BorderLayout.CENTER);
                    contactBookJF.getContentPane().add(createContactJB, BorderLayout.SOUTH);
                    contactBookJF.getContentPane().remove(contactDataJP);
                    contactBookJF.getContentPane().remove(returnMainPageJB);
                    contactBookJF.getContentPane().revalidate();
                    contactBookJF.getContentPane().repaint();
                });
            }
        }

        return new ContactData();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(Main::new);
    }
}