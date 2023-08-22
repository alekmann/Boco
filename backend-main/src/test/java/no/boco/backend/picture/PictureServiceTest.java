package no.boco.backend.picture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
public class PictureServiceTest {
    private Long rightId = 1L;
    private Long wrongId = 2L;

    @InjectMocks
    private PictureService pictureService;
    @Mock
    private PictureRepository pictureRepository;

    @BeforeEach
    public void setup(){
        //delete
        Mockito.lenient().when(pictureRepository.existsById(rightId))
                .thenReturn(true);
        Mockito.lenient().when(pictureRepository.existsById(wrongId))
                .thenReturn(false);
    }

    @Nested
    class delete{
        @Test
        public void throws_error_at_wrong_picture(){
            assertThrows(NoSuchElementException.class, () -> pictureService.delete(wrongId));
        }

        @Test
        public void handles_right_picture(){
            try{
                pictureService.delete(rightId);
            }catch(NoSuchElementException e){
                fail();
            }
        }
    }
}
