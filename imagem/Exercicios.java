package br.pucpr.imagem;

import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Exercicios {
    //Operação realizada em um unico pixel de uma imagem
    public interface OperacaoUnaria{
        Vector3 calcular(Vector3 p);
    }
    //Operação realizada em 2 pixels de 2 imagens
    public interface OperacaoBinaria{
        Vector3 calcular(Vector3 p1,Vector3 p2);
    }
    //Salvar a imagem no disco
    public void salvar(BufferedImage img,String name) throws IOException{
        ImageIO.write(img,"png",new File(name+".png"));
        System.out.printf("Salvo %s.png%n",name);
    }
    //For na imagem aplicando operaçao unaria
    public BufferedImage filtrar(BufferedImage img,OperacaoUnaria op){
        //Cria a saida
        BufferedImage out = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_RGB);

        //Percorre a entrada
        for(int y = 0;y < img.getHeight();y++){
            for(int x = 0;x < img.getWidth();x++){
                //Le o pixel
                Vector3 pixel = new Vector3(img.getRGB(x,y));

                //Aplica a operação passada
                Vector3 o = op.calcular(pixel).clamp();

                //Define a cor na imagem de saida
                out.setRGB(x,y,o.getRGB());
            }
        }
        return out;
    }

    //Faz o for sobre imagem aplicando operação binaria

    public BufferedImage filtrar(BufferedImage img1,BufferedImage img2,OperacaoBinaria op){
        //Garantia de pixels validos acessados
        int w = Math.min(img1.getWidth(),img2.getWidth());
        int h = Math.min(img1.getHeight(),img2.getHeight());

        //Cria a imagem de saida
        BufferedImage out = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);

        //Percorre as imagens
        for(int y = 0;y < h;y++){
            for(int x = 0;x < w;x++){
                //Le os pixels das imagens 1 e 2
                Vector3 p1 = new Vector3(img1.getRGB(x,y));
                Vector3 p2 = new Vector3(img2.getRGB(x,y));

                //Aplica a operação binaria
                Vector3 o = op.calcular(p1,p2).clamp();

                //Define a cor de saida
                out.setRGB(x,y,o.getRGB());
            }
        }
        return out;
    }

    /**
     * Exercício 1
     * Crie uma função BufferedImage bright(BufferedImage img, float intensity)
     * que multiplique todos a cor de todos os pixels da imagem pela intensidade passada.
     * Caso a intensidade seja positiva, deve ser multiplicada diretamente;
     * Caso a intensidade seja negativa, o valor a ser multiplicado é 1 + intensidade. Por exemplo, se a intensidade for
     * -0.2 o valor a ser multiplicado será 0.8.
     *
     * Se o resultado ainda for negativo, use 0 para a intensidade.
     * Se a após a multiplicação do pixel o valor resultante no canal de cor for maior do que 255, grave 255.
     */
    public BufferedImage bright(BufferedImage img, final float intensity) {
        return filtrar(img, p -> p.multiply(intensity < 0 ? 1 + intensity : intensity));
    }

    /**
     * Exercício 2a
     * BufferedImage grayscale(BufferedImage img) que transforme uma imagem colorida em uma imagem em preto e branco.
     * Para isso, defina os valores de r, g e b da imagem de destino iguais ao canal r da imagem de origem
     */
    public BufferedImage grayscaleR(BufferedImage img) {
        return filtrar(img, p -> new Vector3(p.getR(), p.getR(), p.getR()));
    }

    /**
     * Exercício 2b
     * BufferedImage grayscale(BufferedImage img) que transforme uma imagem colorida em uma imagem em preto e branco.
     * Para isso, defina os valores de r, g e b da imagem de destino iguais ao canal r da imagem de origem
     */
    public BufferedImage grayscaleMedia(BufferedImage img) {
        return filtrar(img, p -> {
            float avg = (p.getR() + p.getG() + p.getB()) / 3.0f;
            return new Vector3(avg, avg, avg);
        });
    }

    /**
     * Exercício 2c
     * BufferedImage grayscale(BufferedImage img) que transforme uma imagem colorida em uma imagem em preto e branco.
     * Para isso, defina os valores de r, g e b da imagem de destino iguais a 0.3*r + 0.59*g + 0.11*b da imagem de
     * origem
     */
    public BufferedImage grayscaleFormula(BufferedImage img) {
        return filtrar(img, p -> {
            float avg = p.dot(new Vector3(0.3f, 0.59f, 0.11f));
            return new Vector3(avg, avg, avg);
        });
    }

    /**
     * Exercício 2d
     * Crie a função BufferedImage threshold(BufferedImage img, int value) que pinte de branco todos os pixels
     * maiores ou iguais a value e de preto todos os demais pixels.
     *
     * Considere que img é uma imagem em tons de cinza.
     */
    public BufferedImage threshold(BufferedImage img, final int value) {
        return filtrar(img, p1 -> p1.getR() >= (value / 255.0f) ? new Vector3(Color.WHITE) : new Vector3(Color.BLACK));
    }

    /**
     * Exercício 3a
     * Crie uma função BufferedImage subtract(BufferedImage img1, BufferedImage img2) que receba 2 imagens do mesmo
     * tamanho. Então:
     * Subtraia o pixel (x,y) da imagem 1 do pixel(x,y) da imagem 2;
     * Se o valor resultante no canal de cor for negativo, zere-o;
     */
    public BufferedImage subtract(BufferedImage img1, BufferedImage img2) {
        return filtrar(img1, img2, (p1, p2) -> p1.subtract(p2));
    }

    /**
     * Exercício 3b
     * Faça também uma função add, similar a subtract, mas que soma a cor dos pixels.
     * Novamente, se uma das cores for maior do que 255, force seu valor para 255.
     */
    public BufferedImage add(BufferedImage img1, BufferedImage img2) {
        return filtrar(img1, img2, (p1, p2) -> p1.add(p2));
    }

    /**
     * Exercício 4
     * Faça uma função BufferedImage lerp(BufferedImage img1, BufferedImage img2, float percent) que receba duas
     * imagens de mesmo tamanho e aplique a seguinte fórmula em cada pixel:
     *
     * dst = p1*(1.0f-percent) + p2 * percent
     */
    public BufferedImage lerp(BufferedImage img1, BufferedImage img2, final float percent) {
        return filtrar(img1, img2, (p1, p2) -> p1.multiply(1.0f - percent).add(p2.multiply(percent)));
    }

    /**
     * Exercicio 5
     *
     * Crie a função BufferedImage multiply(BufferedImage img, float[] color) que multiplica cada
     * componente de cor dos pixels de origem pelo componente correspondente da cor passada por parâmetro;
     *
     * Obviamente, essa função deve usar os pixels no intervalo de 0 até 1, como descrito acima.
     * Teste o programa com algumas cores e tente entender o que significa o resultado;
     *
     * Não será necessário criar as funções com floats por o Vector3 já trabalha nesse intervalo
     */
    public BufferedImage multiply(BufferedImage img1, final Vector3 color) {
        return filtrar(img1, p -> p.multiply(color));
    }

    /**
     * Atividade 1
     *  Faça um programa que leia a imagem "/img/cor/puppy.png" e converta suas cores para essa
     palheta de 48 cores do pinterest:

     int[] pallete48 = { //pinterest
     0xD2E3F5, 0x2F401E, 0x3E0A11, 0x4B3316, 0xA5BDE5, 0x87A063,
     0x679327, 0x3A1B0F, 0x928EB1, 0xBFE8AC, 0xA4DA65, 0x5A3810,
     0x47506D, 0x98E0E8, 0x989721, 0x8E762C, 0x0B205C, 0x55BEd7,
     0xB8B366, 0xD8C077, 0x134D9C, 0x2A6E81, 0xE1EAB6, 0xF0DEA6,
     0xFFF3D0, 0x610A0A, 0x7D000E, 0x45164B, 0xFFFCCC, 0x6B330F,
     0x990515, 0x250D3B, 0xB24801, 0x8B4517, 0xE0082D, 0x50105A,
     0xFFF991, 0xB96934, 0xC44483, 0x8E2585, 0xDF5900, 0xF8A757,
     0xC44483, 0xD877CF, 0xFFEF00, 0xDF7800, 0xF847CE, 0xF0A6E8
     };

     Desafio 1: Pesquise sobre o algoritmo de dithering de Floyd- Steinberg e aplique-o.
     */


    public Vector3[] pallete48 = new Vector3[] { //pinterest
            new Vector3(0xD2E3F5), new Vector3(0x2F401E), new Vector3(0x3E0A11), new Vector3(0x4B3316),
            new Vector3(0xA5BDE5), new Vector3(0x87A063), new Vector3(0x679327), new Vector3(0x3A1B0F),
            new Vector3(0x928EB1), new Vector3(0xBFE8AC), new Vector3(0xA4DA65), new Vector3(0x5A3810),
            new Vector3(0x47506D), new Vector3(0x98E0E8), new Vector3(0x989721), new Vector3(0x8E762C),
            new Vector3(0x0B205C), new Vector3(0x55BEd7), new Vector3(0xB8B366), new Vector3(0xD8C077),
            new Vector3(0x134D9C), new Vector3(0x2A6E81), new Vector3(0xE1EAB6), new Vector3(0xF0DEA6),
            new Vector3(0xFFF3D0), new Vector3(0x610A0A), new Vector3(0x7D000E), new Vector3(0x45164B),
            new Vector3(0xFFFCCC), new Vector3(0x6B330F), new Vector3(0x990515), new Vector3(0x250D3B),
            new Vector3(0xB24801), new Vector3(0x8B4517), new Vector3(0xE0082D), new Vector3(0x50105A),
            new Vector3(0xFFF991), new Vector3(0xB96934), new Vector3(0xC44483), new Vector3(0x8E2585),
            new Vector3(0xDF5900), new Vector3(0xF8A757), new Vector3(0xC44483), new Vector3(0xD877CF),
            new Vector3(0xFFEF00), new Vector3(0xDF7800), new Vector3(0xF847CE), new Vector3(0xF0A6E8)
    };


    public static Vector3 findClosestPaletteColor(Vector3 pixel, Vector3[] palette) {
        Vector3 closest = palette[0];

        for (Vector3 n : palette)
            if (n.Difference(pixel) <= closest.Difference(pixel)) {
                closest = n;
                //System.out.println("Veio pro if: ");
                //System.out.println(closest.getRGB());
            }
            else {
                //System.out.println("Veio pro else");
                //System.out.println(closest.getRGB());
            }
        return closest;
    }

    public BufferedImage convertPallete(BufferedImage img, Vector3[] pallete){

        int h = img.getHeight();
        int w = img.getWidth();

        for(int y = 0; y < h;y++){
            for(int x = 0;x < w;x++){
                Vector3 oldColor = new Vector3(img.getRGB(x, y));
                Vector3 newColor = findClosestPaletteColor(oldColor,pallete);
                img.setRGB(x, y, newColor.getRGB());
            }
        }
        return img;
    }

    public BufferedImage floydSteinbergDithering(BufferedImage img,Vector3 [] pallete){

        int w = img.getWidth();
        int h = img.getHeight();

        Vector3[][] d = new Vector3[h][w];

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                d[y][x] = new Vector3(img.getRGB(x, y));

        for(int y = 0; y < h;y++){
            for(int x = 0;x < w;x++){
                Vector3 oldColor = d[y][x];
                Vector3 newColor = findClosestPaletteColor(oldColor,pallete);
                img.setRGB(x, y, newColor.getRGB());

                Vector3 Error = oldColor.subtract(newColor);

                if (x+1 < w)         d[y  ][x+1] = d[y  ][x+1].add(Error.multiply(7.0f/16.0f));
                if (x-1>=0 && y+1<h) d[y+1][x-1] = d[y+1][x-1].add(Error.multiply(3.0f/16.0f));
                if (y+1 < h)         d[y+1][x  ] = d[y+1][x  ].add(Error.multiply(5.0f/16.0f));
                if (x+1<w && y+1<h)  d[y+1][x+1] = d[y+1][x+1].add(Error.multiply(1.0f/16.0f));
            }
        }
        return img;
    }

    /**
     * Crie uma função
     void linha(BufferedImage img, int x1, int y1, int x2,
     int y2, Color color)
     que desenhe uma linha iniciando na posição (x1, y1)
     e indo até a posição (x2, y2).
     */

    public void faz_a_linha(BufferedImage image, int x1, int y1, int x2, int y2, Color color) throws IOException {

        /*BufferedImage out = new BufferedImage(
                image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_RGB);*/

        ImageIO.write(image, "png", new File("resultado_LINHA.jpg"));

        int x = x1;
        int y = y1;

        int Divisor;
        int Dividendo;

        if (x2 - x1 == 0)
            Divisor = 1;
        else if (x2 - x1 > 0)
            Divisor = x2 - x1;
        else
            Divisor = (x2 - x1) * (-1);

        if (y2 - y1 == 0)
            Dividendo = 1;
        else if (y2 - y1 > 0)
            Dividendo = y2 - y1;
        else
            Dividendo = (y2 - y1) * (-1);

        int inclinacao = Dividendo / Divisor;
        if (y2 - y1 == 0)
            inclinacao = Divisor / Dividendo;

        if (x2 > x) {
            for (; x2 > x; x++) {
                if (x % inclinacao == 0){
                    if (y2 > y)
                        y++;
                    else if (y2 < y)
                        y--;
                }
                Color pixel2 = color;

                image.setRGB(x,  y,  pixel2.getRGB());
            }
        }

        else if (x > x2) {
            for (; x > x2; x--) {
                if (x % inclinacao == 0){
                    if (y2 > y)
                        y++;
                    else if (y2 < y)
                        y--;
                }

                Color pixel2 = color;

                image.setRGB(x,  y,  pixel2.getRGB());
            }
        }

        else if (y2 > y) {
            for (; y < y2; y++) {
                if (y % inclinacao == 0){
                    if (x2 > x)
                        x++;
                    else if (x2 < x)
                        x--;
                }

                Color pixel2 = color;

                image.setRGB(x,  y,  pixel2.getRGB());
            }
        }

        else if (y > y2){
            for (; y > y2; y--) {
                if (y % inclinacao == 0){
                    if (x2 > x)
                        x++;
                    else if (x2 < x)
                        x--;
                }

                Color pixel2 = color;

                image.setRGB(x,  y,  pixel2.getRGB());
            }
        }
        ImageIO.write(image, "png", new File("resultado_LINHA.jpg"));
    }



    public void run() throws IOException {
        File PATH = new File("C:/Users/felip/OneDrive/Documentos/Faculdade/Computação Grafica/src/br/pucpr/Imagens");

       /* //Carrega a imagem
        BufferedImage arthas = ImageIO.read(new File(PATH, "arthas.jpg"));

        //Exercício 1: Brightness. Usa 2 valores. Um dobra o brilho e outro diminui em 50%
        salvar(bright(arthas, 2.0f), "ex1ArthasBright");
        salvar(bright(arthas, -0.5f), "ex1ArthasDark");

        //Exercício 2: Gera 3 tipos de grayscale diferentes e o threshold
        salvar(grayscaleR(arthas), "ex2aarthasGrayRed");
        salvar(grayscaleMedia(arthas), "ex2barthasGrayMedia");
        BufferedImage arthasGray = grayscaleFormula(arthas); //Guardamos para usar no threshold
        salvar(arthasGray, "ex2carthasGrayFormula");
        salvar(threshold(arthasGray, 127), "ex2darthasTreshold120");

        //Carrega as imagens dos erros para o exercício 3
        BufferedImage erros1 = ImageIO.read(new File(PATH, "pb/errosB1.png"));
        BufferedImage erros2 = ImageIO.read(new File(PATH, "pb/errosB2.png"));

        //Exercício 3a, subtração
        BufferedImage sub1 = subtract(erros1, erros2);
        BufferedImage sub2 = subtract(erros2, erros1);
        salvar(sub1, "ex3aErrosSubtract1");
        salvar(sub2, "ex3aErrosSubtract2");

        //Exercício 3b, soma
        salvar(add(sub1, sub2), "ex3bErrosSoma");

        //Carga das imagens para o exercício 4
        BufferedImage naruto = ImageIO.read(new File(PATH, "naruto.jpg"));
        BufferedImage sasuke = ImageIO.read(new File(PATH, "sasuke.jpg"));

        //Aplica a função com os coeficientes solicitados
        for (int i = 1; i < 4; i++) {
            salvar(lerp(naruto, sasuke, 0.25f * i), "ex4Lerp" + i);
        }

        //Exercicio 5, multiplicação
        salvar(multiply(arthas, new Vector3(1.0f, 0.5f, 0.5f)), "ex5arthasOnLightRed");
        salvar(multiply(arthas, new Vector3(0.5f, 1.0f, 0.5f)), "ex5arthasOnLightGreen");

        //Atividade 1 palette 48
        BufferedImage puppy = ImageIO.read(new File(PATH,"/cor/puppy.jpg"));
        BufferedImage puppy48 = convertPallete(puppy,pallete48);
        salvar(puppy48,"Puppy_Pallete48");

       //Desafio Floyd Steinberg
        BufferedImage puppy2 = ImageIO.read(new File(PATH,"/cor/puppy.jpg"));
        BufferedImage puppyFloydDithering = floydSteinbergDithering(puppy2,pallete48);
        salvar(puppyFloydDithering,"PuppyFloydDithering");

       //Desafio da linnha
        BufferedImage puppy3 = ImageIO.read(new File(PATH,"/cor/puppy.jpg"));
        faz_a_linha(puppy3,1,1,100,100,Color.yellow);*/
    }

    public static void main(String[] args) throws IOException {
        new Exercicios().run();
    }
}
