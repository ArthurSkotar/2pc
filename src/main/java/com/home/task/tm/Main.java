package com.home.task.tm;

import com.home.task.tm.transactions.Participant;
import com.home.task.tm.transactions.ParticipantImpl;
import com.home.task.tm.transactions.TransactionManager;
import com.home.task.tm.transactions.TransactionManagerImpl;

public class Main {

    private static final int TIME_SECONDS = 5;

    public static void main(String[] args) {
        Participant flyBooking = new ParticipantImpl(DbConnections.getPlaneConnection(), TIME_SECONDS,
                "INSERT INTO public.\"FLY\"(\n" +
                        "\t\"BOOKING_ID\", \"CLIENT_NAME\", \"FLY_NUMBER\", \"FROM\", \"TO\")\n" +
                        "\tVALUES (1, 'test', 'tt1', 'tt', 'tt2');");
        Participant hotelBooking = new ParticipantImpl(DbConnections.getHotelConnection(), TIME_SECONDS,
                "INSERT INTO public.\"HOTEL\"(\n" +
                        "\t\"BOOKING_ID\", \"Client Name\", \"Hotel Name\")\n" +
                        "\tVALUES (1, 'test', 'test_hotel');");
        Participant account = new ParticipantImpl(DbConnections.getAccountConnection(), TIME_SECONDS,
                "UPDATE public.\"ACCOUNT\"\n" +
                        "\tSET \"AMMOUNT\"=\"AMMOUNT\"-100 WHERE \"CLIENT_NAME\" = 'test';");
        TransactionManager tm = new TransactionManagerImpl(TIME_SECONDS, flyBooking, hotelBooking, account);
        tm.start();
    }
}
