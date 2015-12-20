package service;

import entity.Article;
import entity.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

@Path("/files")
@Stateless
public class JerseyFileUpload extends AbstractFacade {

    
    private static final String SERVER_UPLOAD_LOCATION_FOLDER = "../applications/__internal/BlogWS2015";
    //private static final String SERVER_UPLOAD_LOCATION_FOLDER = "C://Users/Florian/Desktop/imgUpload/";     
    //private static final String SERVER_UPLOAD_LOCATION_FOLDER = "/Applications/NetBeans/glassfish-4.1/glassfish/domains/domain1/docroot/uploadedImages/";
 
    @PersistenceContext
    private EntityManager em;

    public JerseyFileUpload() {
        super(Article.class);
    }
    /**
     * Upload a File
     * @param form
     * @return 
     */
    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(FormDataMultiPart form) {
        // Get all parts
        //List<BodyPart> parts = form.getBodyParts();        
        
        System.out.println("UPLOAD MULTIPART FORM");
        // GETTING THE FORM FIELDS
        String titre  = form.getField("titre").getValue();
        String contenu  = form.getField("contenu").getValue();
        
        System.out.println("Titre = " + titre);
        System.out.println("Contenu = " + contenu);
        
        // GETTING THE FILES
        List<FormDataBodyPart> files = form.getFields("file");
        
        System.out.println("Sauvegarde des fichiers suivants : ");
        ArrayList<Image> ListeImages = new ArrayList<Image>();
        if(files != null){
            for (FormDataBodyPart filePart : files) {
                ContentDisposition headerOfFilePart = filePart.getContentDisposition();  
                File output = new File(SERVER_UPLOAD_LOCATION_FOLDER);
                String filePath =  output.getAbsolutePath() + File.separator+ headerOfFilePart.getFileName();
                InputStream fileInputStream = filePart.getValueAs(InputStream.class);
                System.out.println("Fichier : " + filePath);
                // Get the inputStream for the file and save it
                saveFile(fileInputStream, filePath);
                Image img = new Image(filePath);
                super.create(img);
                ListeImages.add(img);
            }
        }
        

        String output = "Files saved to server location using FormDataMultiPart ! ";
        Article a = new Article(titre,contenu);
        a.setImages(ListeImages);
        super.create(a);
        return Response.status(200).entity(output).build();
    }

    // save uploaded file to a defined location on the server
    private void saveFile(InputStream uploadedInputStream, String serverLocation) {

        try {
            OutputStream outpuStream = null;
            int read = 0;
            byte[] bytes = new byte[1024];

            outpuStream = new FileOutputStream(new File(serverLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                outpuStream.write(bytes, 0, read);
            }

            outpuStream.flush();
            outpuStream.close();

            uploadedInputStream.close();
        } catch (IOException e) {

            System.out.println("Erreur dans la sauvegarde du fichier : " + serverLocation);
        }

    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

   
}
