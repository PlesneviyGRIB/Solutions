import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;

public class Task_16 {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {

        if(args.length<1){
            System.out.println("Provide URL!");
            System.exit(0);
        }

        // Открываем сокет для доступа к компьютеру
        // по адресу "java-course.ru" (порт 80)
        URL url = new URL("https://www.google.com");

        Socket s = new Socket(url.toURI().getHost(), 80);

        // Открываем поток для чтения из сокета (информация будет
        // посылаться нам с удаленного компьютера
        InputStream in = s.getInputStream();
        // Открываем поток для записи в сокет (информация будет
        // посылаться от нас на удаленный компьютер
        OutputStream out = s.getOutputStream();

        // Готовим строчку с данными для запроса к серверу
        // Можно пока игнорировать смысл этого запроса
        String str = "GET /network.txt HTTP/1.1\r\n" +
                "Host:" + "www.example.com" + "\r\n\r\n";

        // Превращаем их в массив байт для передачи
        // Мы же используем поток, а он работает с байтами
        byte buf[] = str.getBytes();
        // Пишем в поток вывода
        out.write(buf);

        // И читаем результат в буфер
        int size;
        byte buf_out[] = new byte[1024];
        while ((size = in.read(buf_out)) != -1) {
            System.out.print(new String(buf_out, 0, size));
        }
        s.close();
    }
}